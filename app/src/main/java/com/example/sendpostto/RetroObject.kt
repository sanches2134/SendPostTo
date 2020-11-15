package com.example.sendpostto

import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


object RetroObject {
    private val AUTH="Bearer $accessToken"
    private const val BASEURL="https://api.imgur.com/3/"
    private val okHttp=OkHttpClient.Builder()
            .addInterceptor {
                val original=it.request()
                val boundary = "Boundary-${System.currentTimeMillis()}"
                val requestBuilder=original.newBuilder()
                        .addHeader("Authorization", AUTH)
                       // .addHeader( "Content-Type","multipart/form-data; boundary=$boundary")
                        .method(original.method(),original.body())

                val request=requestBuilder.build()
                it.proceed(request)
            }.build()
    val instance:Inter by lazy{
        val retrofit=Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttp)
                .build()
        retrofit.create(Inter::class.java)
    }
}

