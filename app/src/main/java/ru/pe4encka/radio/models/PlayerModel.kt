package ru.pe4encka.radio.models

import android.text.SpannableString
import android.text.Spanned

object PlayerModel {
    var currentPlay: StationModel? = null
    var currentRecyclerItem: RecyclerModel? = null
    var isPlaying: Boolean = false
    var tit: Spanned? = null

    fun updateTitle(){
        if(tit != null) currentRecyclerItem?.title?.set(tit)
    }

    fun setTitle(title: Spanned){
        tit = title
        updateTitle()
    }

    fun play() {
        currentRecyclerItem?.let {
            it.showDescription.set(true)
            it.showStopButton.set(true)
            it.showProcessPrepare.set(false)
            //it.title.set(SpannableString(""))
            isPlaying = true
        }
    }

    fun stop() {
        isPlaying = false
        currentRecyclerItem?.let {
            it.showDescription.set(false)
            it.showStopButton.set(false)
            it.showProcessPrepare.set(false)
        }
    }

    fun prepare() {
        currentRecyclerItem?.let {
            it.showProcessPrepare.set(true)
            it.title.set(SpannableString(""))
        }
    }

    fun findRecyclerItem(items: List<RecyclerModel>) {
        currentPlay ?: return
        //if (!isPlaying) return

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