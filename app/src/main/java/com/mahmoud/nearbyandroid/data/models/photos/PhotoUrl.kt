package com.mahmoud.nearbyandroid.data.models.photos

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
data class PhotoUrl(@PrimaryKey @ColumnInfo(name = "venueId")val venueId: String, @ColumnInfo(name = "photoUrl") val photoUrl: String)

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhotoUrl(photoUrl: PhotoUrl)

    @Query("select photoUrl from PhotoUrl where venueId like :venueId")
    fun getPhotoUrlLiveData(venueId: String) : LiveData<String?>

    @Query("select photoUrl from PhotoUrl where venueId like :venueId")
    fun getPhotoUrl(venueId: String) : String
}