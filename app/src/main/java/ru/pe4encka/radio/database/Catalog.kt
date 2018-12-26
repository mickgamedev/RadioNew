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
        private var INSTANCE: Catalog? = null
        private var instanceRecent: Catalog? = null
        fun getInstance() = INSTANCE!!
        fun getInstanceRecent() = instanceRecent!!
        fun init(context: Context) {
            INSTANCE = Room.databaseBuilder(context, Catalog::class.java, "catalog.db").build()
            instanceRecent = Room.databaseBuilder(context, Catalog::class.java, "recent.db").build()
        }
    }
}

fun Catalog.Companion.getStations() = getInstance().stationModelDao().getAllStations()
fun Catalog.Companion.addStations(stations: List<StationModel>) = getInstance().stationModelDao().addStations(stations)
fun Catalog.Companion.addRecentStation(station: StationModel) =
    getInstanceRecent().stationModelDao().addStation(station)

fun Catalog.Companion.getRecentStations() = getInstanceRecent().stationModelDao().getRecentStations()