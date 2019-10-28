package com.mahmoud.nearbyandroid.viewmodels

import android.os.AsyncTask
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

class VenueViewModel : PhotoUrlCallback {

    val urlData = MutableLiveData("")
    val errorIcon = MutableLiveData(0)
    private lateinit var venueId: String

    companion object {
        const val IMAGE_WIDTH = 100
        const val IMAGE_HEIGHT = 100

        // Static class not to leak memory
        class RoomAsyncTask(private val callback: PhotoUrlCallback) : AsyncTask<String, Void, String?>() {

            private lateinit var venueId: String

            override fun doInBackground(vararg venueId: String): String? {
                this.venueId = venueId.first()
                return RoomClient.getInstance().databaseInstance?.photoUrlDao()?.getPhotoUrl(venueId = venueId.first())
            }

            override fun onPostExecute(photoUrl: String?) {
                callback.onPhotoUrlReady(photoUrl)
            }
        }
    }

    fun getImageUrl(venueId: String) {
        this.venueId = venueId
        // Get the photo from database
        val task = RoomAsyncTask(this)
        task.execute(venueId)
    }

    private fun getImageUrlFromApi() {
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
                QuotaObserver.isQuotaAvailable = false
            }
        }
    }

    override fun onPhotoUrlReady(photoUrl: String?) {
        if (photoUrl == null) {
            if (QuotaObserver.isQuotaAvailable) {
                Log.d("Mahmoud", "Sending the request")
                getImageUrlFromApi()
            }
        } else {
            urlData.value = photoUrl
        }
    }
}

interface PhotoUrlCallback { fun onPhotoUrlReady(photoUrl: String?) }

object QuotaObserver { var isQuotaAvailable = true }