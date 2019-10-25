package com.mahmoud.nearbyandroid.viewmodels

import android.location.Location
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mahmoud.nearbyandroid.R
import com.mahmoud.nearbyandroid.data.Constants.Companion.CLIENT_ID
import com.mahmoud.nearbyandroid.data.Constants.Companion.CLIENT_SECRET
import com.mahmoud.nearbyandroid.data.Constants.Companion.DATE_VERSION
import com.mahmoud.nearbyandroid.data.models.AppModes
import com.mahmoud.nearbyandroid.data.models.ErrorMessage
import com.mahmoud.nearbyandroid.data.models.venues.ResponseFromServer
import com.mahmoud.nearbyandroid.data.models.venues.Venue
import com.mahmoud.nearbyandroid.data.retrofit.RetrofitClient
import com.mahmoud.nearbyandroid.helpers.NetworkInformation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NearByPlacesViewModel : ViewModel() {

    private var lastLocationSentToServer: Location? = null

    private val networkInformation = NetworkInformation()
    private var appMode: AppModes = AppModes.Realtime
    companion object {
        const val METERS_THRESHOLD = 500
    }

    // Errors
    val errorState: MutableLiveData<ErrorMessage?> = MutableLiveData(null)

    // Visibility
    val placesListVisibilityState: MutableLiveData<Int> = MutableLiveData(View.GONE)
    val errorVisibilityState: MutableLiveData<Int> = MutableLiveData(View.GONE)
    val progressVisibilityState: MutableLiveData<Int> = MutableLiveData(View.GONE)

    // Data
    val venuesData: MutableLiveData<ArrayList<Venue>> = MutableLiveData(ArrayList())

    // Broadcast receivers
    val shouldReceiveLocationBroadCasts: MutableLiveData<Boolean> = MutableLiveData(false)
    val shouldReceiveNetworkBroadCasts: MutableLiveData<Boolean> = MutableLiveData(false)
    val shouldStartLocationUpdate: MutableLiveData<Boolean> = MutableLiveData(false)

    // Menu items
    val menuItemTitle: MutableLiveData<Int> = MutableLiveData(R.string.realtime)

    // region Places logic
    fun getPlaces(location: Location?) {
        showProgress()
        location?.let {currentLocation ->
            // Location is not null
            when(networkInformation.isInternetConnected()) {
                false -> {
                    showError(R.string.no_internet_error, R.drawable.ic_cloud_off)
                    shouldReceiveNetworkBroadCasts.value = true
                }
                else -> {
                    // Internet is available
                    loadPlaces(currentLocation)
                }
            }
            return
        }

        // Location is null
        showError(R.string.no_location_error, R.drawable.ic_location_disabled)
        if (appMode == AppModes.SingleUpdate) {
            shouldReceiveLocationBroadCasts.value = true
        } else {
            shouldStartLocationUpdate.value = true
        }
    }

    private fun loadPlaces(currentLocation: Location) {
        val lat = currentLocation.latitude
        val long = currentLocation.longitude
        val latLong = String.format("%f,%f", lat, long)
        RetrofitClient.getInstance().placesService
            ?.getPlaces(latLong, CLIENT_ID, CLIENT_SECRET, DATE_VERSION)
            ?.enqueue(object : Callback<ResponseFromServer> {

                override fun onFailure(call: Call<ResponseFromServer>, t: Throwable) {
                    showError(R.string.error_wrong, R.drawable.ic_cloud_off)
                }

                override fun onResponse(call: Call<ResponseFromServer>, response: Response<ResponseFromServer>) {
                    when(response.body()) {
                        null -> {showError(R.string.no_response, R.drawable.ic_cloud_off)}
                        else -> { handleResponse(response.body())}
                    }
                }
            })
    }

    private fun handleResponse(response: ResponseFromServer?) {
        response?.let {
            val venues = ArrayList<Venue>()
            it.response.groups[0].items.map { items: Map<String, Any> ->
                val venue = items["venue"] as? Map<String, Any>
                venue?.let{
                    val venueObject =
                        Venue(venue)
                    venues.add(venueObject)
                }
            }
            venuesData.value = venues
            showListView()
            return
        }
    }

    // endregion

    // region Location logic

    fun setCurrentUserLocation(location: Location?) {
        location?.let { currentLocation ->
            val distance = currentLocation.distanceTo(lastLocationSentToServer)
            if (distance > METERS_THRESHOLD) {
                this.lastLocationSentToServer = currentLocation
                loadPlaces(currentLocation)
            }
        }
    }

    // endregion

    // region Visibility manipulators

    private fun showListView() {
        progressVisibilityState.value = View.GONE
        placesListVisibilityState.value = View.VISIBLE
        errorVisibilityState.value = View.GONE
    }

    private fun showProgress() {
        progressVisibilityState.value = View.VISIBLE
        placesListVisibilityState.value = View.GONE
        errorVisibilityState.value = View.GONE
    }

    private fun showError(errorMessage: Int, errorDrawable: Int) {
        progressVisibilityState.value = View.GONE
        placesListVisibilityState.value = View.GONE
        errorVisibilityState.value = View.VISIBLE

        errorState.value = ErrorMessage(errorBodyResource = errorMessage, errorDrawableResource = errorDrawable)
    }

    //endregion

}