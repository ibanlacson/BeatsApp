package com.auf.cea.beatsapp.ui.lyrics

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.auf.cea.beatsapp.R
import com.auf.cea.beatsapp.constants.*
import com.auf.cea.beatsapp.databinding.ActivityLyricsBinding
import com.auf.cea.beatsapp.models.musixmatch.tracksearch.Track
import com.auf.cea.beatsapp.models.shazammodels.MusicDetectionModel
import com.auf.cea.beatsapp.services.helpers.DatabaseOperationsManager
import com.auf.cea.beatsapp.services.helpers.LyricsManager

class LyricsActivity : AppCompatActivity(),
    View.OnClickListener,
    LyricsManager, DatabaseOperationsManager {
    private lateinit var binding : ActivityLyricsBinding
    private lateinit var lyricsData : MusicDetectionModel

    private lateinit var responseType : String
    private lateinit var artist : String
    private lateinit var trackTitle : String
    private lateinit var trackID : String
    private lateinit var isrc :String
    private lateinit var userID: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLyricsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // sharedPreferences
        val sharedPreferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        userID = sharedPreferences.getString(USER_ID,"").toString()

        // Checks whether the response came from MxM or Shazam API.
        if(intent.extras != null) {

            // Retrieve Response Type
            responseType = intent.getStringExtra(RESPONSE_TYPE).toString()

            // If the response came from Shazam
            if (responseType == "shazam"){
                lyricsData = intent.getSerializableExtra(SHAZAM_MODEL_DATA) as MusicDetectionModel

                // Retrieve Parameters
                isrc = lyricsData.track.isrc
                trackTitle = lyricsData.track.title
                artist = lyricsData.track.subtitle
                trackID = lyricsData.matches[0].id

                // DATABASE CALL
                checkFavorite(userID, trackID, isrc, "shazam", binding)

                // Change Views
                with(binding) {
                    txtTrackTitle.text = trackTitle
                    txtTrackArtist.text = artist
                }

                // PARSE LYRICS IN ARRAY FORM TO STRING
                val parsedLyrics = parseShazamResponse(lyricsData)
                if (parsedLyrics.isNullOrEmpty()) {
                    // call MusixMatch API
                    getLyricsDataViaISRC(isrc, trackTitle, artist, binding)
                    Toast.makeText(this,"No Shazam lyrics found! Attempting to search an available lyrics.",Toast.LENGTH_SHORT).show()
                } else {

                    binding.txtLyrics.text = parsedLyrics
                }


            }
            // If the response came from MxM
            else if (responseType == "musixmatch") {

                val mxmTrackData = intent.getSerializableExtra(MXM_MODEL_DATA) as Track

                // RETRIEVE PARAMETERS
                isrc = ""
                trackTitle = mxmTrackData.track.track_name
                artist =  mxmTrackData.track.artist_name
                trackID = mxmTrackData.track.track_id.toString()

                // CONFIGURING CODE
                with(binding){
                    txtTrackTitle.text = trackTitle
                    txtTrackArtist.text = artist
                }

                // DATABASE CALLS
                if(mxmTrackData.track.has_lyrics != 0){
                    getLyricsData(trackID,binding)
                } else {
                    binding.txtLyrics.text = "We're sorry, there's no available lyrics found in the database."
                }
                checkFavorite(userID, mxmTrackData.track.track_id.toString(), "","musixmatch", binding)

            }
            // if the response came from Realm
            else {
                val trackData = intent.getSerializableExtra(TRACK_DATA) as com.auf.cea.beatsapp.models.realm.Track

                // Retrieve Parameters
                isrc = trackData.isrcCode
                trackTitle = trackData.trackTitle
                artist =  trackData.artist
                trackID = trackData.trackID
                var source = trackData.source
//                responseType = trackData.source

                with(binding){
                    txtTrackTitle.text = trackTitle
                    txtTrackArtist.text = artist
                }

                // checkFavorite
                checkFavorite(userID,trackID,isrc,source,binding)

                if(source == "musixmatch") {
                    getLyricsData(trackID,binding)
                } else {
                    getSongDetails(trackID,binding)
                }
            }
        }

        // Configuring Listeners
        with(binding){
            toggleFavorite.setOnClickListener(this@LyricsActivity)
        }
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            (R.id.toggle_favorite) -> {
                val toggleState = binding.toggleFavorite.isChecked

                if (toggleState) {
                    addToFav(artist, trackTitle,responseType, trackID,isrc, userID, this)
                } else {
                    removeToFav(trackID,binding,this)
                }
            }
        }
    }
}