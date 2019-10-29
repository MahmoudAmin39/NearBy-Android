package com.mahmoud.nearbyandroid.data.models.venues

data class Venue(val id: String?, val name: String?, val location: String?, var photoUrl: String?) {

    constructor(map: Map<String, Any>) : this(map["id"] as? String, map["name"] as? String,
        (map["location"] as Map<String, Any>)["address"] as? String, null)
}