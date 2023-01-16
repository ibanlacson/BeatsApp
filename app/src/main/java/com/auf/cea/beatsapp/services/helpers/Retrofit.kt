package com.auf.cea.beatsapp.services.helpers

import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.reflect.Type


object Retrofit {
    fun getInstance(apiURL:String): Retrofit {
        return Retrofit.Builder().baseUrl(apiURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
