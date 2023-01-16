package com.auf.cea.beatsapp.services.realm.operations

import com.auf.cea.beatsapp.models.realm.Track
import com.auf.cea.beatsapp.services.realm.database.TracksRealm
import com.auf.cea.beatsapp.services.realm.database.UserRealm
import io.realm.Case
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.executeTransactionAwait
import io.realm.kotlin.where
import kotlinx.coroutines.Dispatchers

class TracksDbOperations(private var config: RealmConfiguration) {

    suspend fun insertTracks(artist:String, trackTitle:String,
                             source: String, trackID: String,
                             isrcCode:String, userID:String){
        val realm = Realm.getInstance(config)
        realm.executeTransactionAwait(Dispatchers.IO) { realmTransactions ->
            val track = TracksRealm(artist = artist, trackTitle = trackTitle, source = source,
            trackID = trackID, isrcCode = isrcCode)
            realmTransactions.insert(track)

            if (userID.isNotEmpty()){
                val userResult = realmTransactions
                    .where(UserRealm::class.java)
                    .equalTo("id",userID,Case.SENSITIVE)
                    .findFirst()

                userResult?.tracks?.add(track)
            }
        }
    }

    suspend fun deleteTrack(id: String){
        val realm = Realm.getInstance(config)
        realm.executeTransactionAwait(Dispatchers.IO) { realmTransaction ->
            val trackToRemove = realmTransaction
                .where(TracksRealm::class.java)
                .equalTo("id",id,Case.SENSITIVE)
                .findFirst()

            trackToRemove?.deleteFromRealm()
        }
    }
    suspend fun deleteTrackViaTrackID(trackID: String){
        val realm = Realm.getInstance(config)
        realm.executeTransactionAwait(Dispatchers.IO) { realmTransaction ->
            val trackToRemove = realmTransaction
                .where(TracksRealm::class.java)
                .equalTo("trackID",trackID,Case.SENSITIVE)
                .findFirst()

            trackToRemove?.deleteFromRealm()
        }
    }


    suspend fun retrieveTracks(id:String):Track?{
        val realm = Realm.getInstance(config)
        var realmResults = arrayListOf<Track>()
        realm.executeTransactionAwait(Dispatchers.IO) { realmTransactions ->
             realmResults.addAll(realmTransactions
                .where(TracksRealm::class.java)
                .equalTo("id", id, Case.SENSITIVE)
                .findAll()
                 .map {
                     mapTracks(it)
                 }
            )
        }

        val returningResult: Track? = if (realmResults.size != 0) {
            realmResults[0]
        } else {
            null
        }
        return returningResult
    }


    private fun mapTracks(tracks: TracksRealm):Track {
        return Track(
            id = tracks.id,
            artist = tracks.artist,
            trackTitle = tracks.trackTitle,
            source = tracks.source,
            trackID = tracks.trackID,
            isrcCode = tracks.isrcCode,
            user = tracks.user?.firstOrNull()?.id ?: ""
        )
    }
}