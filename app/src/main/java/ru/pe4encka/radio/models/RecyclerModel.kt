package ru.pe4encka.radio.models

import androidx.databinding.ObservableBoolean

class RecyclerModel(val station: StationModel) {
    val showDescription = ObservableBoolean(false)
    val showStopButton = ObservableBoolean(false)
    val showProcessPrepare = ObservableBoolean(false)
}
