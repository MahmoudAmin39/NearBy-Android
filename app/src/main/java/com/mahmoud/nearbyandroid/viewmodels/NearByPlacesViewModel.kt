package com.mahmoud.nearbyandroid.viewmodels

import android.content.Context.MODE_PRIVATE
import android.location.Location
import android.view.View
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mahmoud.nearbyandroid.NearByApp
import com.mahmoud.nearbyandroid.R
import com.mahmoud.nearbyandroid.data.Constants
import com.mahmoud.nearbyandroid.data.Constants.Companion.APPMODE_REALTIME
import com.mahmoud.nearbyandroid.data.Constants.Companion.APPMODE_SINGLE_UPDATE
import com.mahmoud.nearbyandroid.data.Constants.Companion.ERROR_GOOGLE_PLAY_CONNECTION_FAILED
import com.mahmoud.nearbyandroid.data.Constants.Companion.ERROR_NO_INTERNET
import com.mahmoud.nearbyandroid.data.Constants.Companion.ERROR_NO_LOCATION
import com.mahmoud.nearbyandroid.data.Constants.Companion.ERROR_PERMISSION_DENIED
import com.mahmoud.nearbyandroid.data.models.ErrorMessage
import com.mahmoud.nearbyandroid.data.models.venues.Venue
import com.mahmoud.nearbyandroid.data.retrofit.VenuesApiClient
import com.mahmoud.nearbyandroid.helpers.LocationInformation
import com.mahmoud.nearbyandroid.helpers.NetworkInformation

class NearByPlacesViewModel : ViewModel(), VenuesCallback {

    private val apiClient = VenuesApiClient(this)
    private var lastLocationSentToServer: Location? = null
    private val sharedPrefs =
        NearByApp.appContext?.getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE)

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

    // App mode
    val appMode: MutableLiveData<Int?> = MutableLiveData(null)

    // region checkInternet

    fun checkConditions() {
        // 1) Check internet
        if (!NetworkInformation().isInternetConnected()) {
            // Internet is not available
            showError(R.string.no_internet_error, R.drawable.ic_cloud_off, ERROR_NO_INTERNET)
        } else {
            // Internet is available
            // 2) Check GPS
            if (LocationInformation().isGPSAvailable()) {
                // GPS Available
                getAppMode()
            } else {
                // GPS is not available
                showError(
                    R.string.no_location_error,
                    R.drawable.ic_location_disabled,
                    ERROR_NO_LOCATION
                )
            }
        }
    }

    // endregion


    // region App Mode logic

    private fun getAppMode() {
        appMode.value =
            sharedPrefs?.getInt(Constants.APP_MODE, APPMODE_REALTIME) ?: APPMODE_REALTIME
    }

    private fun setAppMode() {
        sharedPrefs?.edit { putInt(Constants.APP_MODE, appMode.value!!) }
    }

    fun onMenuItemClicked() {
        appMode.value = if (appMode.value == APPMODE_REALTIME) {
            APPMODE_SINGLE_UPDATE
        } else {
            APPMODE_REALTIME
        }

        setAppMode()
    }

    // endregion

    // region Places logic

    fun loadPlaces() {
        lastLocationSentToServer?.let { apiClient.getPlacesFromServer(it) }
    }

    override fun onVenuesReady(venues: ArrayList<Venue>) {
        venuesData.value = venues
        showListView()
    }
    // endregion

    // region Location logic

    fun setCurrentUserLocation(location: Location?) {
        location?.let { currentLocation ->
            // The first location sent to the view model
            if (lastLocationSentToServer == null) {
                this.lastLocationSentToServer = currentLocation
                apiClient.getPlacesFromServer(currentLocation)
            } else {
                val distance = currentLocation.distanceTo(lastLocationSentToServer)
                if (distance > METERS_THRESHOLD) {
                    this.lastLocationSentToServer = currentLocation
                    apiClient.getPlacesFromServer(currentLocation)
                }
            }
            return
        }

        // Location is null for any reasons
        if (!LocationInformation().isGPSAvailable()) {
            // GPS is disabled
            showError(
                R.string.no_location_error,
                R.drawable.ic_location_disabled,
                ERROR_NO_LOCATION
            )
        } else {
            // GPS is enabled
            if (!NetworkInformation().isInternetConnected()) {
                showError(R.string.no_internet_error, R.drawable.ic_cloud_off, ERROR_NO_INTERNET)
            }
        }
    }

    // endregion

    // region Error callbacks

    fun onPermissionDenied() {
        showError(
            R.string.error_permission_access,
            R.drawable.ic_location_disabled,
            ERROR_PERMISSION_DENIED
        )
    }

    fun onConnectionToGooglePlayFailed() {
        showError(
            R.string.error_wrong,
            R.drawable.ic_cloud_off,
            ERROR_GOOGLE_PLAY_CONNECTION_FAILED
        )
    }

    // endregion

    // region Visibility manipulators

    private fun showListView() {
        progressVisibilityState.value = View.GONE
        placesListVisibilityState.value = View.VISIBLE
        errorVisibilityState.value = View.GONE
    }

    override fun showProgress() {
        progressVisibilityState.value = View.VISIBLE
        placesListVisibilityState.value = View.GONE
        errorVisibilityState.value = View.GONE
    }

    override fun showError(errorMessage: Int, errorDrawable: Int, errorCode: Int) {
        progressVisibilityState.value = View.GONE
        placesListVisibilityState.value = View.GONE
        errorVisibilityState.value = View.VISIBLE

        errorState.value = ErrorMessage(
            errorBodyResource = errorMessage,
            errorDrawableResource = errorDrawable,
            errorCode = errorCode
        )
    }

    //endregion
}

interface VenuesCallback {

    fun onVenuesReady(venues: ArrayList<Venue>)
    fun showProgress()
    fun showError(errorMessage: Int, errorDrawable: Int, errorCode: Int)
}