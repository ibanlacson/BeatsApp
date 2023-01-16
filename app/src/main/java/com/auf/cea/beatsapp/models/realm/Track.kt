package com.auf.cea.beatsapp.models.realm

data class Track(
    val id: String,
    val artist: String,
    val trackTitle:String,
    val source:String,
    val trackID:String,
    val isrcCode:String,
    val user:String = ""
):java.io.Serializable
