package com.auf.cea.beatsapp

import android.app.Application
import io.realm.Realm

class BeatsApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}