package ru.pe4encka.radio.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
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
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var path: String
    private var readyToPlay = false

    private var notificationTitle: Spanned? = null
    private var notificationBitmap: Bitmap? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG_FOREGROUND_SERVICE, "My foreground service onCreate().")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_FOREGROUND_SERVICE -> {
                    path = it.getStringExtra(EXTRA_FILENAME_ID)
                    startForegroundService()
                    prepare()
                }
                ACTION_STOP_FOREGROUND_SERVICE -> {
                    stopForegroundService()
                    stop()
                    clear()
                }
                ACTION_PLAY -> {
                    play()
                }
                ACTION_PAUSE -> {
                    pause()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun prepare() {
        readyToPlay = false
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        mediaPlayer?.apply {
            setOnPreparedListener { preparedListener() }
            setDataSource(path)
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            prepareAsync()
        }
        PlayerModel.prepare()
        notificationTitle = null
        notificationBitmap = null
        if(PlayerModel.currentPlay != null) {
            val src = PlayerModel.currentPlay!!.logo
            if (!src.isEmpty()) Picasso.get().load(src).into(object: Target{
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    if(bitmap != null) {
                        notificationBitmap = bitmap
                        updateNotification()
                    }
                }

            })
        }
    }

    private fun preparedListener() {
        readyToPlay = true
        play()
        PlayerModel.play()
    }

    private fun clear() {
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
            .setOnNewMetadataListener(object : OnNewMetadataListener {
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
                    val title = Html.fromHtml(streamTitle ?: "")
                    PlayerModel.setTitle(title)
                    notificationTitle = title
                    updateNotification()
                }

            })
            .setUserAgent(UserAgent.WINDOWS_MEDIA_PLAYER)
            .start()
    }

    private fun createNotification(): Notification {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("radio", "Radio Service")
            } else {
                ""
            }
        val builder = NotificationCompat.Builder(this, channelId)
        createNotificationBuilder(builder)
        return builder.build()
    }

    private fun updateNotification() {
        val notification = createNotification()
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(1, notification);
    }

    private fun startForegroundService() {
        val notification = createNotification()
        // Start foreground service.
        startForeground(1, notification)
    }

    private fun createNotificationBuilder(builder: NotificationCompat.Builder) {
        val playIntent = Intent(this, RadioService::class.java)
        playIntent.action = ACTION_PLAY
        val pendingIntentPlay = PendingIntent.getService(this, 0, playIntent, 0)

        val pauseIntent = Intent(this, RadioService::class.java)
        pauseIntent.action = ACTION_PAUSE
        val pendingIntentPause = PendingIntent.getService(this, 0, pauseIntent, 0)

        val stopIntent = Intent(this, RadioService::class.java)
        stopIntent.action = ACTION_STOP_FOREGROUND_SERVICE
        val pendingIntentStop = PendingIntent.getService(this, 0, stopIntent, 0)

        val activityIntent = Intent(this, MainActivity::class.java)
        val pendingIntentActivity =
            PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val remoteViews = RemoteViews(packageName, R.layout.notification_service).apply{
            PlayerModel.currentPlay?.let {
                setTextViewText(R.id.stationName, it.name)

            }
            notificationTitle?.let {
                if(it.length > 0) setTextViewText(R.id.textTitle,it.toString())
                else setTextViewText(R.id.textTitle,"")
            }
            notificationBitmap?.let{
                setImageViewBitmap(R.id.imageLogo,it)
            }


            setOnClickPendingIntent(R.id.root, pendingIntentActivity)
            setOnClickPendingIntent(R.id.imagePlay, pendingIntentPlay)
            setOnClickPendingIntent(R.id.imagePause, pendingIntentPause)
            setOnClickPendingIntent(R.id.imageStop, pendingIntentStop)

        }

        builder.apply {
            setOnlyAlertOnce(true)
            setOngoing(true)
            setShowWhen(false)
            setSmallIcon(R.drawable.ic_radio_icon)
            setCustomBigContentView(remoteViews)
        }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
}
