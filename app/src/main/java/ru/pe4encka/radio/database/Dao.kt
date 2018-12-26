package ru.pe4encka.radio.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Observable
import ru.pe4encka.radio.models.StationModel

@Dao
interface StationModelDao {
    @Query("SELECT * FROM stations")
    fun getAllStations(): List<StationModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addStations(stations: List<StationModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addStation(station: StationModel)

    @Query("SELECT * FROM stations WHERE recent=1")
    fun getRecentStations(): Observable<List<StationModel>>

    //TODO сделать удалени избранных станций через обновнение флага recent
}
