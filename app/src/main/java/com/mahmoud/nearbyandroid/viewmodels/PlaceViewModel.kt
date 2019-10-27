package com.mahmoud.nearbyandroid.viewmodels

import android.util.Log
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

class PlaceViewModel {

    val errorIcon = MutableLiveData(0)
    val errorText = MutableLiveData("")
    private lateinit var venueId: String

    companion object {
        const val IMAGE_WIDTH = 100
        const val IMAGE_HEIGHT = 100
    }

    fun getImageUrl(venueId: String) {
        this.venueId = venueId
/*        // Get it from database first
        val asyncTask = RoomAsyncTask(this)
        asyncTask.execute(venueId)*/
        Log.d("Mahmoud", "Sending request")
        RetrofitClient.getInstance()
            .placesService?.getPhotos(venueId, CLIENT_ID, CLIENT_SECRET, DATE_VERSION)
            ?.enqueue(object : Callback<PhotoResponseFromServer> {
                override fun onFailure(call: Call<PhotoResponseFromServer>, t: Throwable) {
                    errorIcon.value = R.drawable.ic_error
                    errorText.value = t.localizedMessage
                }

                override fun onResponse(
                    call: Call<PhotoResponseFromServer>,
                    response: Response<PhotoResponseFromServer>
                ) {
                    Log.d("Mahmoud", call.request().url().toString())
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

                    // Save the URL to Database which will emit a value (LiveData)
                    val photoUrl = PhotoUrl(venueId, imageUrlString)
                    val client = RoomClient.getInstance()
                    val database = client.databaseInstance
                    val dao = database!!.photoUrlDao()
                    dao.insertPhotoUrl(photoUrl)
                }
                return
            }
        } else {
            errorIcon.value = R.drawable.ic_error
            if (response.code() == 429) {
                errorText.value = "Quota exceeded"
            } else {
                errorText.value = response.message()
            }
        }
    }
}