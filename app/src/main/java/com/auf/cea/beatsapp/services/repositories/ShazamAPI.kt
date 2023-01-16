package com.auf.cea.beatsapp.services.repositories

import com.auf.cea.beatsapp.models.shazammodels.MusicDetectionModel
import com.auf.cea.beatsapp.models.shazammodels.songdetectmodel.SongDetailModel
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ShazamAPI {
    //Detect Song
    @POST("/songs/detect")
    @Headers(
        "Content-Type:text/plain",
        "X-RapidAPI-Key:89a3738eaemsh25143a27863ee5fp1dc522jsne60055db3eed",
        "X-RapidAPI-Host:shazam.p.rapidapi.com")
    suspend fun detectMusic(
       @Body base64EncodedString: RequestBody
    ):Response<MusicDetectionModel>

    // Get Song Details
    @GET("/songs/get-details")
    @Headers(
        "X-RapidAPI-Key:89a3738eaemsh25143a27863ee5fp1dc522jsne60055db3eed",
        "X-RapidAPI-Host:shazam.p.rapidapi.com")
    suspend fun getSongDetails(
        @Query("key") key:String,
        @Query("locale") locale:String
    ):Response<SongDetailModel>
}
