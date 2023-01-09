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

//class GsonStringConverterFactory : Converter.Factory() {
//    fun toRequestBody(type: Type, annotations: Array<Annotation?>?): Converter<*, RequestBody>? {
//        return if (String::class.java == type) {
//            object : Converter<String?, RequestBody?>() {
//                @Throws(IOException::class)
//                fun convert(value: String?): RequestBody {
//                    return RequestBody.create(MEDIA_TYPE, value)
//                }
//            }
//        } else null
//    }
//
//    companion object {
//        private val MEDIA_TYPE: MediaType = MediaType.parse("text/plain")
//    }
//}