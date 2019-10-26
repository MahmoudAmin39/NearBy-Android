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
import com.mahmoud.nearbyandroid.data.Constants.Companion.CLIENT_ID
import com.mahmoud.nearbyandroid.data.Constants.Companion.CLIENT_SECRET
import com.mahmoud.nearbyandroid.data.Constants.Companion.DATE_VERSION
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
    private val sharedPrefs = NearByApp.appContext?.getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE)
    private val networkInformation = NetworkInformation()
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

    // Menu items
    val appMode: MutableLiveData<Int> = MutableLiveData(APPMODE_REALTIME)

    // region App Mode logic

    fun getAppMode() {
        appMode.value = sharedPrefs?.getInt(Constants.APP_MODE, APPMODE_REALTIME) ?: APPMODE_REALTIME
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

    // region Places logic

    private fun loadPlaces(currentLocation: Location) {
        when(networkInformation.isInternetConnected()) {
            false -> { showError(R.string.no_internet_error, R.drawable.ic_cloud_off) }
            else -> { getPlacesFromServer(currentLocation)}
        }
    }

    private fun getPlacesFromServer(currentLocation: Location) {
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
                        null -> { showError(R.string.no_response, R.drawable.ic_cloud_off) }
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
                    }
                }
                venuesData.value = venues
                showListView()
                return
            }
        } else {
            showError(R.string.error_wrong, R.drawable.ic_cloud_off)
        }
    }

    // endregion

    // region Location logic

    fun setCurrentUserLocation(location: Location?) {
        location?.let { currentLocation ->
            if (lastLocationSentToServer == null) {
                this.lastLocationSentToServer = currentLocation
                loadPlaces(currentLocation)
            } else {
                val distance = currentLocation.distanceTo(lastLocationSentToServer)
                if (distance > METERS_THRESHOLD) {
                    this.lastLocationSentToServer = currentLocation
                    loadPlaces(currentLocation)
                }
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