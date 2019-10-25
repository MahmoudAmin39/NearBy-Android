package com.mahmoud.nearbyandroid.data.models.photos

import com.google.gson.annotations.SerializedName

data class Photos(@SerializedName("items") val photoItems: ArrayList<PhotoItem>)