package ru.pe4encka.radio.repository

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.JSON
import org.apache.commons.io.IOUtils
import ru.pe4encka.radio.R
import ru.pe4encka.radio.models.StationModel
import java.nio.charset.Charset

object Repository{
    var stations: List<StationModel>? = null
    private lateinit var data: String

    fun init(context: Context) = with(context) {
        Log.v("Start", "Start loadind stations")
        resources.openRawResource(R.raw.locate).use {
            data = IOUtils.toString(it, Charset.forName("UTF-8"))
        }
        Log.v("Start", "End loadind stations")

    }

    suspend fun parseStationList() {
        Log.v("Start", "Start parse stations")
        stations = JSON.parse(StationModel.serializer().list, data)
        Log.v("Start", "End parse stations")
    }
}