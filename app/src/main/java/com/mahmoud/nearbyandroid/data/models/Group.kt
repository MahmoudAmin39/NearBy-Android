package com.mahmoud.nearbyandroid.data.models

import com.google.gson.annotations.SerializedName

data class Group(@SerializedName("items") val items: ArrayList<Map<String, Any>>)