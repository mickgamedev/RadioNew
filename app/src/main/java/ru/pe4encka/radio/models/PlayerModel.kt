package ru.pe4encka.radio.models

import android.text.SpannableString
import android.text.Spanned

object PlayerModel {
    var currentPlay: StationModel? = null
    var currentRecyclerItem: RecyclerModel? = null
    var isPlaying: Boolean = false
    var streamTitle: Spanned? = null

    fun updateTitle(){
        if(streamTitle != null) currentRecyclerItem?.title?.set(streamTitle)
    }

    fun setTitle(title: Spanned){
        streamTitle = title
        updateTitle()
    }

    fun play() {
        currentRecyclerItem?.apply {
            showDescription.set(true)
            showStopButton.set(true)
            showProcessPrepare.set(false)
            isPlaying = true
        }
    }

    fun stop() {
        isPlaying = false
        currentRecyclerItem?.apply {
            showDescription.set(false)
            showStopButton.set(false)
            showProcessPrepare.set(false)
        }
    }

    fun prepare() {
        currentRecyclerItem?.apply {
            showProcessPrepare.set(true)
            title.set(SpannableString(""))
        }
    }

    fun findRecyclerItem(items: List<RecyclerModel>) {
        currentPlay ?: return

        for (it in items) {
            if (it.station == currentPlay) {
                currentRecyclerItem = it
                updateTitle()
                if (isPlaying) play() else stop()
                break
            }
        }
    }
}