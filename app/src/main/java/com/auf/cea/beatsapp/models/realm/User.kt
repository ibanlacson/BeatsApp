package com.auf.cea.beatsapp.models.realm

import com.auf.cea.beatsapp.services.realm.database.TracksRealm
import io.realm.RealmList

data class User(
    val id:String,
    val firstName: String,
    val lastName: String,
    val track: RealmList<TracksRealm>
)
