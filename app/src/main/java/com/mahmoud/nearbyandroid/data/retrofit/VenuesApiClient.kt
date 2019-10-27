package com.mahmoud.nearbyandroid.data.retrofit

import android.location.Location
import com.mahmoud.nearbyandroid.R
import com.mahmoud.nearbyandroid.data.Constants
import com.mahmoud.nearbyandroid.data.models.venues.ResponseFromServer
import com.mahmoud.nearbyandroid.data.models.venues.Venue
import com.mahmoud.nearbyandroid.viewmodels.PlaceViewModel
import com.mahmoud.nearbyandroid.viewmodels.VenuesCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VenuesApiClient(private val callback: VenuesCallback) {

    fun getPlacesFromServer(currentLocation: Location) {
        callback.showProgress()
        val lat = currentLocation.latitude
        val long = currentLocation.longitude
        val latLong = String.format("%f,%f", lat, long)
        RetrofitClient.getInstance().placesService
            ?.getPlaces(latLong,
                Constants.CLIENT_ID,
                Constants.CLIENT_SECRET,
                Constants.DATE_VERSION
            )
            ?.enqueue(object : Callback<ResponseFromServer> {

                override fun onFailure(call: Call<ResponseFromServer>, t: Throwable) {
                    callback.showError(
                        R.string.error_wrong, R.drawable.ic_cloud_off,
                        Constants.ERROR_NO_RESPONSE
                    )
                }

                override fun onResponse(call: Call<ResponseFromServer>, response: Response<ResponseFromServer>) {
                    when(response.body()) {
                        null -> { callback.showError(
                            R.string.no_response, R.drawable.ic_cloud_off,
                            Constants.ERROR_NO_RESPONSE
                        ) }
                        else -> { handleResponse(response)}
                    }
                }
            })
    }

    private fun handleResponse(response: Response<ResponseFromServer>) {
        if (response.isSuccessful && response.code() == 200) {
            response.body()?.let {
                val venues = ArrayList<Venue>()
                it.response.groups[0].items.map { items: Map<String, Any> ->
                    val venue = items["venue"] as? Map<String, Any>
                    venue?.let{
                        val venueObject =
                            Venue(venue)
                        venues.add(venueObject)
                        // Tell the Places view model to get the Photo url
                        val venueViewModel = PlaceViewModel()
                        venueViewModel.getImageUrl(venueObject.id!!)
                    }
                }

                callback.onVenuesReady(venues)
                return
            }
        } else {
            callback.showError(R.string.error_wrong, R.drawable.ic_cloud_off, Constants.ERROR_NO_RESPONSE)
        }
    }
}