package com.mahmoud.nearbyandroid.viewmodels

import androidx.lifecycle.MutableLiveData
import com.mahmoud.nearbyandroid.data.Constants.Companion.CLIENT_ID
import com.mahmoud.nearbyandroid.data.Constants.Companion.CLIENT_SECRET
import com.mahmoud.nearbyandroid.data.Constants.Companion.DATE_VERSION
import com.mahmoud.nearbyandroid.data.models.photos.PhotoResponseFromServer
import com.mahmoud.nearbyandroid.data.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaceViewModel {

    val imageUrl = MutableLiveData("")

    fun getImageUrl(venueId: String) {
        RetrofitClient.getInstance()
            .placesService?.getPhotos(venueId, CLIENT_ID, CLIENT_SECRET, DATE_VERSION)?.enqueue(object : Callback<PhotoResponseFromServer> {
            override fun onFailure(call: Call<PhotoResponseFromServer>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<PhotoResponseFromServer>,
                response: Response<PhotoResponseFromServer>
            ) {
                val photo = response.body()?.response?.photos!!.photoItems[0]
                val imageUrlString = String.format("%s100x100%s", photo.prefix, photo.suffix)
                imageUrl.value = imageUrlString
            }
        })
    }
}