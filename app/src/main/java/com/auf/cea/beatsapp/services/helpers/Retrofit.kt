package com.auf.cea.beatsapp.services.helpers

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {
    fun getInstance(apiURL:String): Retrofit {
        return Retrofit.Builder().baseUrl(apiURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}