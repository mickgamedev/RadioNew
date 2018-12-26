package ru.pe4encka.radio.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "stations")
data class StationModel(
    @PrimaryKey val id: Int,
    val name:String,
    val desc:String,
    val icon:String,
    val slogan:String,
    val tags: String,
    val stream:String,
    val logo:String,
    val language:String,
    val format: String,
    val locate: String
)
