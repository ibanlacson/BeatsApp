package com.auf.cea.beatsapp.services.realm.operations

import com.auf.cea.beatsapp.models.realm.Track
import com.auf.cea.beatsapp.models.realm.User
import com.auf.cea.beatsapp.services.realm.database.TracksRealm
import com.auf.cea.beatsapp.services.realm.database.UserRealm
import io.realm.Case
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.executeTransactionAwait
import kotlinx.coroutines.Dispatchers

class UserDbOperations(private var config: RealmConfiguration) {
    suspend fun retrieveUserData(id:String):User?{
        val realm = Realm.getInstance(config)
        val realmResults = arrayListOf<User>()
        realm.executeTransactionAwait(Dispatchers.IO) { realmTransaction ->
             realmResults.addAll(realmTransaction
                 .where(UserRealm::class.java)
                 .equalTo("id", id, Case.SENSITIVE )
                 .findAll()
                 .map {
                     mapUser(it)
                 })
        }

        val returningResult: User? = if (realmResults.size != 0) {
            realmResults[0]
        } else {
            null
        }

        return returningResult
    }

    suspend fun filterTrackData(userID:String, trackID:String, isrcCode:String, source:String): ArrayList<User>{
        val realm = Realm.getInstance(config)
        val realmResults = arrayListOf<User>()

        realm.executeTransactionAwait(Dispatchers.IO) { realmTransactions ->
            realmResults.addAll(realmTransactions
                .where(UserRealm::class.java)
                .equalTo("id",userID,Case.SENSITIVE)
                .equalTo("tracks.trackID",trackID, Case.SENSITIVE)
                .equalTo("tracks.isrcCode",isrcCode, Case.SENSITIVE)
                .equalTo("tracks.source",source, Case.SENSITIVE)
                .findAll()
                .map {
                    mapUser(it)
                })
        }
        return realmResults
    }

    suspend fun updateUser(userID: String, firstName: String, lastName: String){
        val realm = Realm.getInstance(config)
        realm.executeTransactionAwait(Dispatchers.IO) { realmTransactions ->
            val userResult = realmTransactions
                .where(UserRealm::class.java)
                .equalTo("id",userID,Case.SENSITIVE)
                .findFirst()

            userResult?.firstName = firstName
            userResult?.lastName = lastName

        }
    }

    suspend fun insertNewUser(id: String, firstName:String, lastName:String){
        val realm = Realm.getInstance(config)
        realm.executeTransactionAwait(Dispatchers.IO) { realmTransaction ->
            val user = UserRealm(id = id, firstName = firstName, lastName = lastName)
            realmTransaction.insert(user)
        }
    }

    private fun mapUser(user: UserRealm): User{
        return User(
            id = user.id,
            firstName = user.firstName,
            lastName = user.lastName,
            track = user.tracks
        )
    }
}
