package com.mahmoud.nearbyandroid.data.models

import com.google.gson.annotations.SerializedName

data class Response(@SerializedName("groups") val groups: ArrayList<Group>)