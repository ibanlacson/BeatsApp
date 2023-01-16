package com.auf.cea.beatsapp.services.realm.database

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmField
import io.realm.annotations.Required
import org.bson.types.ObjectId

open class TracksRealm(
    @PrimaryKey
    var id:String = ObjectId().toHexString(),

    @Required
    var artist:String = "",
    @Required
    var trackTitle:String ="",
    @Required
    var source:String = "",

    var trackID: String ="",
    var isrcCode: String ="",

    @LinkingObjects("tracks")
    val user: RealmResults<UserRealm>? = null

): RealmObject()
