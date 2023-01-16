package com.auf.cea.beatsapp.services.helpers

import android.annotation.SuppressLint
import android.util.Log
import com.auf.cea.beatsapp.constants.MXM_API_KEY
import com.auf.cea.beatsapp.constants.MXM_BASE_URL
import com.auf.cea.beatsapp.constants.SHAZAM_BASE_URL
import com.auf.cea.beatsapp.databinding.ActivityLyricsBinding
import com.auf.cea.beatsapp.models.shazammodels.MusicDetectionModel
import com.auf.cea.beatsapp.models.shazammodels.songdetectmodel.SongDetailModel
import com.auf.cea.beatsapp.services.realm.config.RealmConfig
import com.auf.cea.beatsapp.services.repositories.MusixmatchAPI
import com.auf.cea.beatsapp.services.repositories.ShazamAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface LyricsManager {
    //  TURNS THE LYRICS RESPONSE FROM SHAZAM API (SONG-DETECT ENDPOINT) TO USABLE STRING FOR DISPLAY
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

    //  TURNS THE LYRICS RESPONSE FROM SHAZAM API (SONG-DETAIL ENDPOINT) TO USABLE STRING FOR DISPLAY
    fun parseLyricsShazam(data:SongDetailModel):String?{
        val section = data.sections
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
    @SuppressLint("SetTextI18n")
    fun getLyricsData(trackID:String, binding: ActivityLyricsBinding) {
        val mxmAPI = Retrofit.getInstance(MXM_BASE_URL).create(MusixmatchAPI::class.java)
        GlobalScope.launch(Dispatchers.IO) {
            try{
                val result = mxmAPI.getLyrics(MXM_API_KEY,trackID)
                Log.d("API CALL RESULT:",result.toString())
                Log.d("TRACK ID PASSED:", trackID)
                val lyricsDataResult = result.body()
                if (lyricsDataResult != null){
                    withContext(Dispatchers.Main){
                        Log.d("SOMETHING: ", lyricsDataResult.message.toString())
                        if (lyricsDataResult.message.header.status_code == 200){
                            val lyrics = lyricsDataResult.message.body.lyrics.lyrics_body
                            binding.txtLyrics.text = lyrics
                        } else {
                            binding.txtLyrics.text = "We're sorry, there's no available lyrics found in the database."
                        }
                    }
                } else {
                    Log.e("ERROR:", "NULL RESPONSE")
                }
            } catch (e: RuntimeException){
                withContext(Dispatchers.Main){
                    binding.txtLyrics.text = "We're sorry, there's no available lyrics found in the database."
                }
            }
        }
    }

    // GET LYRICS DATA FROM SHAZAM API
    fun getSongDetails(key:String, binding: ActivityLyricsBinding){
        val shazamAPI = Retrofit.getInstance(SHAZAM_BASE_URL).create(ShazamAPI::class.java)
        GlobalScope.launch (Dispatchers.IO) {
            val result = shazamAPI.getSongDetails(key,"en-US")
            val songDataResult = result.body()
            if (songDataResult != null) {
                withContext(Dispatchers.Main){
                    if (result.code() == 200){
                        withContext(Dispatchers.Main){
                            with(binding){
                                txtTrackTitle.text = songDataResult.title
                                txtTrackArtist.text = songDataResult.subtitle

                                if (parseLyricsShazam(songDataResult)!=null){
                                    txtLyrics.text = parseLyricsShazam(songDataResult)
                                } else {
                                    val isrc = songDataResult.isrc
                                    val trackTitle = songDataResult.title
                                    val artist = songDataResult.subtitle

                                    getLyricsDataViaISRC(isrc, trackTitle, artist, binding)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}