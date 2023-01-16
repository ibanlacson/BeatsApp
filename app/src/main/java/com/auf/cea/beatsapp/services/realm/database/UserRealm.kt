package com.auf.cea.beatsapp.services.realm.database

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class UserRealm(
    @PrimaryKey
    var id: String = "",
    @Required
    var firstName:String = "",
    var lastName:String = "",
    var tracks: RealmList<TracksRealm> = RealmList()
):RealmObject()
