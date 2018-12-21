package ru.pe4encka.radio.repository

import android.content.Context
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list
import org.apache.commons.io.IOUtils
import ru.pe4encka.radio.R
import ru.pe4encka.radio.models.StationModel
import java.nio.charset.Charset

object Repository{
    var stations: List<StationModel>? = null
    private lateinit var data: String

    fun init(context: Context) = with(context) {
        resources.openRawResource(R.raw.stations).use {
            data = IOUtils.toString(it, Charset.forName("UTF-8"))
        }
    }

    suspend fun parseStationList() {
        stations = JSON.parse(StationModel.serializer().list, data)
    }
}