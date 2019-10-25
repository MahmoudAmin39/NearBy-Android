package com.mahmoud.nearbyandroid.data.models.photos

import com.google.gson.annotations.SerializedName

data class PhotoResponseFromServer(@SerializedName("response") val response: Response)