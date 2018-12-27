package ru.pe4encka.radio.models

import android.text.SpannableString
import android.text.Spanned
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField

class RecyclerModel(val station: StationModel) {
    val showDescription = ObservableBoolean(false)
    val showStopButton = ObservableBoolean(false)
    val showProcessPrepare = ObservableBoolean(false)
    val showLike = ObservableBoolean(station.recent)
    val title = ObservableField<Spanned>(SpannableString(""))
}
