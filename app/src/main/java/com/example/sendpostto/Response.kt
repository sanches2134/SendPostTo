package com.example.sendpostto

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class Response(
@SerializedName("id") val id : String,
@SerializedName("title") val title : String,
@SerializedName("description") val description : String,
@SerializedName("datetime") val datetime : Int,
@SerializedName("type") val type : String,
@SerializedName("animated") val animated : Boolean,
@SerializedName("width") val width : Int,
@SerializedName("height") val height : Int,
@SerializedName("size") val size : Int,
@SerializedName("views") val views : Int,
@SerializedName("bandwidth") val bandwidth : Int,
@SerializedName("vote") val vote : String,
@SerializedName("favorite") val favorite : Boolean,
@SerializedName("nsfw") val nsfw : String,
@SerializedName("section") val section : String,
@SerializedName("account_url") val account_url : String,
@SerializedName("account_id") val account_id : Int,
@SerializedName("is_ad") val is_ad : Boolean,
@SerializedName("in_most_viral") val in_most_viral : Boolean,
@SerializedName("tags") val tags : List<String>,
@SerializedName("ad_type") val ad_type : Int,
@SerializedName("ad_url") val ad_url : String,
@SerializedName("in_gallery") val in_gallery : Boolean,
@SerializedName("deletehash") val deletehash : String,
@SerializedName("name") val name : String,
@SerializedName("link") val link : String

)