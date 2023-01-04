package com.auf.cea.beatsapp.models.MusixMatchModels.GetLyricsModel

data class Lyrics(
    var explicit: Int,
    var lyrics_body: String,
    var lyrics_copyright: String,
    var lyrics_id: Int,
    var pixel_tracking_url: String,
    var script_tracking_url: String,
    var updated_time: String
)