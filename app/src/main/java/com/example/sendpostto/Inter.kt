package com.example.sendpostto

import android.graphics.Bitmap
import android.net.Uri
import retrofit2.Call
import retrofit2.http.*

interface Inter {

    @FormUrlEncoded
    @POST("upload/")
    fun uploadPost(
        @Field("image") image:Bitmap,
        @Field("title") title:String,
        @Field("description") description:String

    ):Call<Response>

}