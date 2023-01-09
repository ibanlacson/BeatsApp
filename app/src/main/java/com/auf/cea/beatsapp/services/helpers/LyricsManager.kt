package com.auf.cea.beatsapp.services.helpers

import android.util.Log
import com.auf.cea.beatsapp.constants.MXM_API_KEY
import com.auf.cea.beatsapp.constants.MXM_BASE_URL
import com.auf.cea.beatsapp.databinding.ActivityLyricsBinding
import com.auf.cea.beatsapp.models.shazammodels.MusicDetectionModel
import com.auf.cea.beatsapp.services.repositories.MusixmatchAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface LyricsManager {


    //  TURNS THE LYRICS RESPONSE FROM SHAZAM API TO USABLE STRING FOR DISPLAY
    fun parseShazamResponse(data:MusicDetectionModel):String?{
        val section = data.track.sections
        val parsedLyrics:String?
        var index =  -1

        // Get the index of the Lyrics Data
        for (i in section.indices){
            Log.d("Section types FOUND:", section[i].type)
            if (section[i].type == "LYRICS"){
                index = i
                Log.d("INDEX FOUND:", i.toString())
                break
            }
        }

        parsedLyrics = if (index == -1) {
            null
        } else {
            section[index].text.joinToString("\n")
        }

        return parsedLyrics
    }

    // GET LYRICS DATA FROM MUSIXMATCH API THROUGH ISRC, TRACK, & ARTIST DATA FROM SHAZAM API
    fun getLyricsDataViaISRC(isrc:String, track:String, artist: String, binding: ActivityLyricsBinding) {
        val mxmAPI = Retrofit.getInstance(MXM_BASE_URL).create(MusixmatchAPI::class.java)
        GlobalScope.launch(Dispatchers.IO) {
            val result = mxmAPI.getLyricsViaISRC(MXM_API_KEY,isrc,track,artist)
            val lyricsDataResult = result.body()
            if (lyricsDataResult != null){
                withContext(Dispatchers.Main){
                    val lyrics = lyricsDataResult.message.body.lyrics.lyrics_body
                    binding.txtLyrics.text = lyrics
                }
            } else {
                Log.e("ERROR:", "NULL RESPONSE")
            }
        }
    }

    // GET LYRICS DATA FROM MUSIXMATCH API THROUGH SERIALIZED TRACK_ID FROM SEARCH FRAGMENT
    fun getLyricsData(trackID:String, binding: ActivityLyricsBinding) {
        val mxmAPI = Retrofit.getInstance(MXM_BASE_URL).create(MusixmatchAPI::class.java)
        GlobalScope.launch(Dispatchers.IO) {
            val result = mxmAPI.getLyrics(MXM_API_KEY,trackID)
            val lyricsDataResult = result.body()
            if (lyricsDataResult != null){
                withContext(Dispatchers.Main){
                    val lyrics = lyricsDataResult.message.body.lyrics.lyrics_body
                    binding.txtLyrics.text = lyrics
                }
            } else {
                Log.e("ERROR:", "NULL RESPONSE")
            }
        }
    }


}