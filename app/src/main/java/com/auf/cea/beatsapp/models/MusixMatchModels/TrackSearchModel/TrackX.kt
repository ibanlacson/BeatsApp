package com.auf.cea.beatsapp.models.MusixMatchModels.TrackSearchModel

data class TrackX(
    var album_id: Int,
    var album_name: String,
    var artist_id: Int,
    var artist_name: String,
    var commontrack_id: Int,
    var explicit: Int,
    var has_lyrics: Int,
    var has_richsync: Int,
    var has_subtitles: Int,
    var instrumental: Int,
    var num_favourite: Int,
    var primary_genres: PrimaryGenres,
    var restricted: Int,
    var track_edit_url: String,
    var track_id: Int,
    var track_name: String,
    var track_name_translation_list: List<Any>,
    var track_rating: Int,
    var track_share_url: String,
    var updated_time: String
)