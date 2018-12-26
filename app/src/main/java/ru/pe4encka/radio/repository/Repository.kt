package ru.pe4encka.radio.repository

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list
import org.apache.commons.io.IOUtils
import ru.pe4encka.radio.R
import ru.pe4encka.radio.database.*
import ru.pe4encka.radio.models.StationModel
import java.nio.charset.Charset

object Repository{
    var stations: List<StationModel>? = null
    private lateinit var data: String
    var currentRecyclerPosition: Int = 0

    fun init(context: Context) = with(context) {
        Log.v("Start", "Start loadind stations")
        resources.openRawResource(R.raw.index).use {
            data = IOUtils.toString(it, Charset.forName("UTF-8"))
        }
        Log.v("Start", "End loadind stations")
        loadRecyclerPosition(this)
    }

    suspend fun parseStationList() {
        if (stations != null) return
        val list = Catalog.getStations()
        if (list.size == 0) {
            stations = JSON.parse(StationModel.serializer().list, data)
            Catalog.addStations(Repository.stations!!)
        } else stations = list
    }

    suspend fun addRecentStation(station: StationModel) {
        Catalog.addRecentStation(station)
    }

    fun getRecentObserver() = Catalog.getRecentStations()

    fun saveRecyclerPosition(context: Context) = context.apply {
        getSharedPreferences("settings.txt", Context.MODE_PRIVATE).edit()
            .putInt("recyclerPosition", currentRecyclerPosition)
            .apply()
    }

    fun loadRecyclerPosition(context: Context) = context.apply {
        getSharedPreferences("settings.txt", Context.MODE_PRIVATE).apply {
            currentRecyclerPosition = getInt("recyclerPosition", 0)
        }
    }
}