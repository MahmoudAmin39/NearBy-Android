package com.mahmoud.nearbyandroid.viewmodels

import android.location.Location
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mahmoud.nearbyandroid.R
import com.mahmoud.nearbyandroid.data.models.AppModes
import com.mahmoud.nearbyandroid.data.models.ErrorMessage
import com.mahmoud.nearbyandroid.helpers.LocationInfoProvider
import com.mahmoud.nearbyandroid.helpers.NetworkInformation

class NearByPlacesViewModel : ViewModel() {

    private var lastLocationSentToServer: Location? = null

    private val networkInformation = NetworkInformation()
    private val locationInfoProvider = LocationInfoProvider()
    private var appMode: AppModes = AppModes.Realtime
    companion object {
        const val THRESHOLD = 500
    }

    // Errors
    val errorState: MutableLiveData<ErrorMessage?> = MutableLiveData(null)
    val alertMessage: MutableLiveData<Int> = MutableLiveData(0)

    // Visibility
    val placesListVisibilityState: MutableLiveData<Int> = MutableLiveData(View.GONE)
    val errorVisibilityState: MutableLiveData<Int> = MutableLiveData(View.GONE)
    val progressVisibilityState: MutableLiveData<Int> = MutableLiveData(View.GONE)

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
                    // TODO: Send the request
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

    // region Location logic
    fun setLocationSentToServer(location: Location?) {
        location?.let {
            this.lastLocationSentToServer = location
            if (!locationInfoProvider.isLocationEnabled()) {
                // The last known location maybe not the current
                alertMessage.value = R.string.no_location_snackbar
                shouldReceiveLocationBroadCasts.value = true
            }
            return
        }

    }

    fun setCurrentUserLocation(location: Location?) {
        location?.let { currentLocation ->
            val distance = currentLocation.distanceTo(lastLocationSentToServer)
            if (distance > THRESHOLD) {
                this.lastLocationSentToServer = currentLocation
                // TODO: Send a request to Foursquare API
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