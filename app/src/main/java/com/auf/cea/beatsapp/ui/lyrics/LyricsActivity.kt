package com.auf.cea.beatsapp.ui.lyrics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.auf.cea.beatsapp.constants.MXM_MODEL_DATA
import com.auf.cea.beatsapp.constants.RESPONSE_TYPE
import com.auf.cea.beatsapp.constants.SHAZAM_MODEL_DATA
import com.auf.cea.beatsapp.constants.TRACK_ID
import com.auf.cea.beatsapp.databinding.ActivityLyricsBinding
import com.auf.cea.beatsapp.models.musixmatch.tracksearch.Track
import com.auf.cea.beatsapp.models.shazammodels.MusicDetectionModel
import com.auf.cea.beatsapp.services.helpers.LyricsManager

class LyricsActivity : AppCompatActivity(), LyricsManager {
    private lateinit var binding : ActivityLyricsBinding
    private lateinit var lyricsData : MusicDetectionModel
    private lateinit var responseType : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLyricsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.extras != null) {
            responseType = intent.getStringExtra(RESPONSE_TYPE).toString()
            if (responseType == "shazam"){
                lyricsData = intent.getSerializableExtra(SHAZAM_MODEL_DATA) as MusicDetectionModel
                val parsedLyrics = parseShazamResponse(lyricsData)
                if (parsedLyrics.isNullOrEmpty()) {
                    // Retrieve Parameters
                    val isrc = lyricsData.track.isrc
                    val track = lyricsData.track.title
                    val artist = lyricsData.track.subtitle

                    // call MusixMatch API
                    getLyricsDataViaISRC(isrc, track, artist, binding)

                    Toast.makeText(this,"No Shazam lyrics found! Attempting to search an available lyrics.",Toast.LENGTH_SHORT).show()
                } else {
                    binding.txtLyrics.text = parsedLyrics
                }
            } else {
                val trackID = intent.getStringExtra(TRACK_ID)
                val mxmTrackData = intent.getSerializableExtra(MXM_MODEL_DATA) as Track

                // call MusixMatch API
                if (trackID != null) {
                    getLyricsData(trackID,binding)
                }

            }
        }

    }
}