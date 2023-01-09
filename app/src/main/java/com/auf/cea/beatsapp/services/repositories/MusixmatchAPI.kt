package com.auf.cea.beatsapp.services.repositories

import com.auf.cea.beatsapp.models.musixmatch.getlyrics.GetLyricsModel
import com.auf.cea.beatsapp.models.musixmatch.tracksearch.TrackSearchModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MusixmatchAPI {
    @GET("/ws/1.1/track.search")
    suspend fun searchTrack(
        @Query("apikey") apikey: String,
        @Query("q_track_artist") q:String,
        @Query("page") page:Int,
        @Query("page_size") size:Int
    ):Response<TrackSearchModel>

    @GET("/ws/1.1/track.lyrics.get")
    suspend fun getLyrics(
        @Query("apikey") apikey: String,
        @Query("track_id") track_id:String,
    ):Response<GetLyricsModel>
}