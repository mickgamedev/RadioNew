package ru.pe4encka.radio.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.pe4encka.radio.models.StationModel

@Database(
    entities = arrayOf(
        StationModel::class
    ), version = 1, exportSchema = false
)
abstract class Catalog : RoomDatabase() {
    abstract fun stationModelDao(): StationModelDao

    companion object {
        private var inst: Catalog? = null
        fun getInstance() = inst!!
        fun init(context: Context) {
            inst = Room.databaseBuilder(context, Catalog::class.java, "catalog.db").build()
        }
    }
}

fun Catalog.Companion.getStations() = getInstance().stationModelDao().getAllStations()
fun Catalog.Companion.addStations(stations: List<StationModel>) = getInstance().stationModelDao().addStations(stations)
fun Catalog.Companion.updateRecentStation(station: StationModel) =
    getInstance().stationModelDao().addStation(station)

fun Catalog.Companion.getRecentStations() = getInstance().stationModelDao().getRecentStations()