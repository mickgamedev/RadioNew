package ru.pe4encka.radio.models

object PlayerModel {
    var currentPlay: StationModel? = null
    var currentRecyclerItem: RecyclerModel? = null
    var isPlaying: Boolean = false

    fun play() {
        currentRecyclerItem?.let {
            it.showDescription.set(true)
            it.showStopButton.set(true)
            it.showProcessPrepare.set(false)
            isPlaying = true
        }

    }

    fun stop() {
        currentRecyclerItem?.let {
            it.showDescription.set(false)
            it.showStopButton.set(false)
            it.showProcessPrepare.set(false)
            isPlaying = false
        }
    }

    fun prepare() {
        currentRecyclerItem?.let {
            it.showProcessPrepare.set(true)
        }
    }

    fun findRecyclerItem(items: List<RecyclerModel>) {
        currentPlay ?: return
        if (!isPlaying) return

        for (it in items) {
            if (it.station == currentPlay) {
                currentRecyclerItem = it
                play()
                break
            }
        }

    }
}