package ru.pe4encka.radio.models

import kotlinx.serialization.Serializable

@Serializable
data class StationModel(
    val name:String,
    val desc:String,
    val icon:String,
    val slogan:String,
    val tags:List<String>,
    val stream:String,
    val logo:String,
    val language:String,
    val format:String
)
