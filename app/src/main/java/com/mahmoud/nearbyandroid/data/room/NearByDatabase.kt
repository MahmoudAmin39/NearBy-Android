package com.mahmoud.nearbyandroid.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mahmoud.nearbyandroid.data.models.photos.PhotoDao
import com.mahmoud.nearbyandroid.data.models.photos.PhotoUrl

@Database(entities = [PhotoUrl::class], version = 1, exportSchema = false)
abstract class NearByDatabase : RoomDatabase() {

    abstract fun photoUrlDao(): PhotoDao
}