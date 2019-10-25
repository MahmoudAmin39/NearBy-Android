package com.mahmoud.nearbyandroid.data.models.photos

import com.google.gson.annotations.SerializedName

data class PhotoItem(@SerializedName("id") val id: String,
                     @SerializedName("prefix") val prefix: String,
                     @SerializedName("suffix") val suffix: String)