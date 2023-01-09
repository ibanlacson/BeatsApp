package com.auf.cea.beatsapp.models.shazammodels

data class MusicDetectionModel(
    var matches: List<Matche>,
    var tagid: String,
    var timestamp: Long,
    var timezone: String,
    var track: Track
)