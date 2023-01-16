package com.auf.cea.beatsapp.services.helpers

import android.util.Log
import android.widget.Toast
import com.auf.cea.beatsapp.databinding.ActivityLyricsBinding
import com.auf.cea.beatsapp.models.realm.Track
import com.auf.cea.beatsapp.services.realm.config.RealmConfig
import com.auf.cea.beatsapp.services.realm.operations.TracksDbOperations
import com.auf.cea.beatsapp.services.realm.operations.UserDbOperations
import com.auf.cea.beatsapp.ui.adapters.RealmTrackAdapter
import com.auf.cea.beatsapp.ui.adapters.TrackListAdapter
import com.auf.cea.beatsapp.ui.lyrics.LyricsActivity
import kotlinx.coroutines.*

interface DatabaseOperationsManager {
    // CHECK THE DB IF THE ENTRY IS FAVORITE
    fun checkFavorite(userID:String, trackID: String, isrcCode:String, source:String, binding:ActivityLyricsBinding ){
        val realmConfiguration = RealmConfig.getConfiguration()
        val userDbOperations = UserDbOperations(realmConfiguration)
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("FilterTrackData"))

        scope.launch (Dispatchers.IO) {
            val result = userDbOperations.filterTrackData(userID, trackID, isrcCode, source)
            if(result.size != 0) {
                withContext(Dispatchers.Main){
                    binding.toggleFavorite.isChecked = true
                    scope.coroutineContext.cancel()
                }
            }
        }
    }

    // INSERT TO FAVORITE FROM LYRICS ACTIVITY
    fun addToFav(
        artist: String, trackTitle: String,
        source: String, trackID: String,
        isrcCode: String, userID: String, activity: LyricsActivity
    ) {
        val realmConfiguration = RealmConfig.getConfiguration()
        val trackDbOperations = TracksDbOperations(realmConfiguration)
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("AddToFavorite"))
        scope.launch (Dispatchers.IO) {
            withContext(Dispatchers.Main){
                trackDbOperations.insertTracks(artist, trackTitle, source, trackID, isrcCode, userID)
                Toast.makeText(activity,"Track added to favorites!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // REMOVE TO FAVORITE FROM LYRICS ACTIVITY
    fun removeToFav(trackID: String, binding: ActivityLyricsBinding,activity: LyricsActivity){
        val realmConfiguration = RealmConfig.getConfiguration()
        val trackDbOperations = TracksDbOperations(realmConfiguration)
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("RemoveTrackViaTrackIDFromLyricsActivity"))
        scope.launch (Dispatchers.IO){
            trackDbOperations.deleteTrackViaTrackID(trackID)
            withContext(Dispatchers.Main){
                Toast.makeText(activity,"Track removed from favorites!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // RETRIEVE TRACKS
    fun retrieveTracks(userID: String, adapter: RealmTrackAdapter) {
        val realmConfiguration = RealmConfig.getConfiguration()
        val userDbOperations = UserDbOperations(realmConfiguration)
        val trackDbOperations = TracksDbOperations(realmConfiguration)
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("RetrieveTracks"))
        val trackList: ArrayList<Track> = arrayListOf()
        scope.launch (Dispatchers.IO) {
            val result = userDbOperations.retrieveUserData(userID)
            if (result != null) {
                for(i in result.track.indices){
                    val trackResult = trackDbOperations.retrieveTracks(result.track[i]!!.id)
                    if (trackResult != null) {
                        trackList.add(trackResult)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                adapter.updateData(trackList)
            }
        }
    }

    // REMOVE TRACKS TO FAVORITE LIST
    fun removeTracks(id:String, userID: String, adapter: RealmTrackAdapter){
        val realmConfiguration = RealmConfig.getConfiguration()
        val trackDbOperations = TracksDbOperations(realmConfiguration)
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("RetrieveTracks"))
        scope.launch (Dispatchers.IO){
            trackDbOperations.deleteTrack(id)
            withContext(Dispatchers.Main){
                retrieveTracks(userID, adapter)
            }
        }
    }
}