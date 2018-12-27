package ru.pe4encka.radio.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.TimedMetaData
import android.media.TimedText
import android.net.Uri
import android.os.IBinder
import android.text.Html
import android.util.Log
import androidx.core.app.NotificationCompat
import com.vodyasov.amr.AudiostreamMetadataManager
import com.vodyasov.amr.OnNewMetadataListener
import com.vodyasov.amr.UserAgent
import ru.pe4encka.radio.R
import ru.pe4encka.radio.models.PlayerModel
import ru.pe4encka.radio.ui.MainActivity

val TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE"
val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
val ACTION_PAUSE = "ACTION_PAUSE"
val ACTION_PLAY = "ACTION_PLAY"
val EXTRA_FILENAME_ID = "FILENAME_ID"


class RadioService : Service() {
    //private lateinit var music: MusicSong
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var path: String
    private var readyToPlay = false

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG_FOREGROUND_SERVICE, "My foreground service onCreate().")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_FOREGROUND_SERVICE -> {
                    //music = MusicSong(it.getStringExtra(EXTRA_FILENAME_ID)).apply { extractMetaData() }
                    path = it.getStringExtra(EXTRA_FILENAME_ID)
                    startForegroundService()
                    //Toast.makeText(applicationContext, "Foreground service is started.", Toast.LENGTH_SHORT).show()
                    prepare()
                    //play()
                }
                ACTION_STOP_FOREGROUND_SERVICE -> {
                    stopForegroundService()
                    //Toast.makeText(applicationContext, "Foreground service is stopped.", Toast.LENGTH_SHORT).show()
                    stop()
                    clear()
                }
                ACTION_PLAY -> {
                    //Toast.makeText(applicationContext, "You click Play button.", Toast.LENGTH_SHORT).show()
                    play()
                }
                ACTION_PAUSE -> {
                    //Toast.makeText(applicationContext, "You click Pause button.", Toast.LENGTH_SHORT).show()
                    pause()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun prepare(){
        readyToPlay = false
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        setMediaPlayerListeners()
        mediaPlayer?.setDataSource(path)
        //mmr.setDataSource(path)
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer?.prepareAsync()
        PlayerModel.prepare()
    }

    private fun setMediaPlayerListeners() {
        mediaPlayer?.setOnPreparedListener { preparedListener() }
        mediaPlayer?.setOnTimedMetaDataAvailableListener { _, data ->  metaDataListener(data)}
        mediaPlayer?.setOnTimedTextListener { _, text ->  timedTextListener(text)}
    }

    private fun timedTextListener(text: TimedText){

    }

    private fun metaDataListener(meta: TimedMetaData){
        val data = meta.metaData
    }

    private fun preparedListener() {
        readyToPlay = true
        play()
        PlayerModel.play()
    }

    private fun clear(){
        mediaPlayer?.release()
    }

    private fun stop() {
        mediaPlayer?.stop()
        AudiostreamMetadataManager.getInstance().stop()
    }

    private fun pause() {
        mediaPlayer?.pause()
        AudiostreamMetadataManager.getInstance().stop()
    }

    private fun play() {
        if (readyToPlay) mediaPlayer?.start()
        AudiostreamMetadataManager.getInstance()
            .setUri(Uri.parse(path))
            .setOnNewMetadataListener(object: OnNewMetadataListener{
                override fun onNewHeaders(
                    stringUri: String?,
                    name: MutableList<String>?,
                    desc: MutableList<String>?,
                    br: MutableList<String>?,
                    genre: MutableList<String>?,
                    info: MutableList<String>?
                ) {

                }

                override fun onNewStreamTitle(stringUri: String?, streamTitle: String?) {
                    val title = Html.fromHtml(streamTitle?: "")
                    PlayerModel.setTitle(title)
                }

            })
            .setUserAgent(UserAgent.WINDOWS_MEDIA_PLAYER)
            .start()
    }

    /* Used to build and start foreground service. */
    private fun startForegroundService() {
        // Create notification builder.
        val builder = NotificationCompat.Builder(this)

        createNotification(builder)

        // Add Play button intent in notification.
        val playIntent = Intent(this, RadioService::class.java)
        playIntent.action = ACTION_PLAY
        val pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0)
        val playAction = NotificationCompat.Action(R.drawable.ic_play, "Play", pendingPlayIntent)
        builder.addAction(playAction)

        // Add Pause button intent in notification.
        val pauseIntent = Intent(this, RadioService::class.java)
        pauseIntent.action = ACTION_PAUSE
        val pendingPrevIntent = PendingIntent.getService(this, 0, pauseIntent, 0)
        val prevAction = NotificationCompat.Action(R.drawable.ic_pause, "Pause", pendingPrevIntent)
        builder.addAction(prevAction)

        // Add Stop button intent in notification.
        val stopIntent = Intent(this, RadioService::class.java)
        stopIntent.action = ACTION_STOP_FOREGROUND_SERVICE
        val pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, 0)
        val stopAction = NotificationCompat.Action(R.drawable.ic_stop, "Stop", pendingStopIntent)
        builder.addAction(stopAction)


        val activityIntent = Intent(this, MainActivity::class.java)
        val pendingIntentActivity = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntentActivity)

        // Build the notification.
        val notification = builder.build()

        // Start foreground service.
        startForeground(1, notification)
    }

    private fun createNotification(builder: NotificationCompat.Builder) = builder.apply{
        setSmallIcon(R.drawable.ic_radio_icon)
        //color = getColor(R.color.colorIconNotification)
        setPriority(NotificationCompat.PRIORITY_MAX)
        PlayerModel.currentPlay?.let {
            setContentTitle(it.name)
        }
        //setContentText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE))
        //setContentTitle("Radio")
        //setContentText(music.album)
        //setContentInfo(music.albumartist)
        //setLargeIcon(music.cover)
        setShowWhen(false)
    }

    private fun stopForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.")
        // Stop foreground service and remove the notification.
        stopForeground(true)
        // Stop the foreground service.
        stopSelf()
        PlayerModel.stop()
    }

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not yet implemented");
    }
}
