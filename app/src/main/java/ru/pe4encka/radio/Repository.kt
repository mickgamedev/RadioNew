package ru.pe4encka.radio

import android.content.Context
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list
import org.apache.commons.io.IOUtils
import java.nio.charset.Charset

object Repository{
    lateinit var stations: List<StationModel>

    fun init(context: Context) = with(context) {
        resources.openRawResource(R.raw.stations).use {
            val st = IOUtils.toString(it, Charset.forName("UTF-8"))
            stations = JSON.parse(StationModel.serializer().list, st)
        }
    }


}