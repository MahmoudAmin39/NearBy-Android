package com.mahmoud.nearbyandroid.viewmodels

import androidx.lifecycle.MutableLiveData
import com.mahmoud.nearbyandroid.R
import com.mahmoud.nearbyandroid.data.Constants.Companion.CLIENT_ID
import com.mahmoud.nearbyandroid.data.Constants.Companion.CLIENT_SECRET
import com.mahmoud.nearbyandroid.data.Constants.Companion.DATE_VERSION
import com.mahmoud.nearbyandroid.data.models.photos.PhotoResponseFromServer
import com.mahmoud.nearbyandroid.data.models.photos.PhotoUrl
import com.mahmoud.nearbyandroid.data.retrofit.RetrofitClient
import com.mahmoud.nearbyandroid.data.room.RoomClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VenueViewModel {

    val urlData = MutableLiveData("")
    val errorIcon = MutableLiveData(0)
    private lateinit var venueId: String

    companion object {
        const val IMAGE_WIDTH = 100
        const val IMAGE_HEIGHT = 100
    }

    fun getImageUrlFromApi(venueId: String) {
        this.venueId = venueId
        RetrofitClient.getInstance()
            .placesService?.getPhotos(venueId, CLIENT_ID, CLIENT_SECRET, DATE_VERSION)
            ?.enqueue(object : Callback<PhotoResponseFromServer> {
                override fun onFailure(call: Call<PhotoResponseFromServer>, t: Throwable) {
                    errorIcon.value = R.drawable.ic_error
                }

                override fun onResponse(
                    call: Call<PhotoResponseFromServer>,
                    response: Response<PhotoResponseFromServer>
                ) {
                    handleResponse(response)
                }
            })
    }

    private fun handleResponse(response: Response<PhotoResponseFromServer>) {
        if (response.isSuccessful && response.code() == 200) {
            response.body()?.let {
                if (it.response.photos.photoItems.isNotEmpty()) {
                    val photo = it.response.photos.photoItems[0]
                    val imageUrlString = String.format(
                        "%s%dx%d%s",
                        photo.prefix,
                        IMAGE_WIDTH,
                        IMAGE_HEIGHT,
                        photo.suffix
                    )
                    // Post the data
                    urlData.value = imageUrlString

                    // Save the URL to Database
                    val photoUrl = PhotoUrl(venueId, imageUrlString)
                    RoomClient.getInstance().databaseInstance?.photoUrlDao()
                        ?.insertPhotoUrl(photoUrl)
                }
                return
            }
        } else {
            errorIcon.value = R.drawable.ic_error
            if (response.code() == 429) {
                QuotaObserver.isQuotaAvailable = false
            }
        }
    }
}

object QuotaObserver {
    var isQuotaAvailable = true
}