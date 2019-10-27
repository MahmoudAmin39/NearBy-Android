package com.mahmoud.nearbyandroid.data.room

import androidx.room.Room
import com.mahmoud.nearbyandroid.NearByApp

class RoomClient private constructor() {

    var databaseInstance: NearByDatabase? = null
        private set

    init {
        if (databaseInstance == null) {
            databaseInstance = Room
                .databaseBuilder<NearByDatabase>(NearByApp.appContext!!, NearByDatabase::class.java, "NearBy Database")
                .allowMainThreadQueries()
                .build()
        }
    }

    companion object {
        private var instance: RoomClient? = null

        fun getInstance(): RoomClient {
            if (instance == null) {
                instance = RoomClient()
            }
            return instance as RoomClient
        }
    }
}