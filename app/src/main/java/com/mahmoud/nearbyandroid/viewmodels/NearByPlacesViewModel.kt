package com.mahmoud.nearbyandroid.viewmodels

import android.location.Location
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mahmoud.nearbyandroid.R
import com.mahmoud.nearbyandroid.data.models.ErrorMessage
import com.mahmoud.nearbyandroid.helpers.LocationInfoProvider
import com.mahmoud.nearbyandroid.helpers.NetworkInformation

class NearByPlacesViewModel : ViewModel() {

    private var lastLocationSentToServer: Location? = null

    private val networkInformation = NetworkInformation()
    private val locationInfoProvider = LocationInfoProvider()

    // Errors
    val errorState: MutableLiveData<ErrorMessage?> = MutableLiveData(null)
    val alertMessage: MutableLiveData<Int> = MutableLiveData(0)

    // Visibility
    val bodyVisibilityState: MutableLiveData<Int> = MutableLiveData(View.GONE)
    val errorVisibilityState: MutableLiveData<Int> = MutableLiveData(View.GONE)
    val progressVisibilityState: MutableLiveData<Int> = MutableLiveData(View.GONE)

    // Broadcast receivers
    val shouldReceiveLocationBroadCasts: MutableLiveData<Boolean> = MutableLiveData(false)
    val shouldReceiveNetworkBroadCasts: MutableLiveData<Boolean> = MutableLiveData(false)

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
        // Location is null
        showError(R.string.no_location_error, R.drawable.ic_location_disabled)
        shouldReceiveLocationBroadCasts.value = true
    }

    // endregion



    // region Visibility manipulators
    private fun showBody() {
        progressVisibilityState.value = View.GONE
        bodyVisibilityState.value = View.VISIBLE
        errorVisibilityState.value = View.GONE
    }

    private fun showProgress() {
        progressVisibilityState.value = View.VISIBLE
        bodyVisibilityState.value = View.GONE
        errorVisibilityState.value = View.GONE
    }

    private fun showError(errorMessage: Int, errorDrawable: Int) {
        progressVisibilityState.value = View.GONE
        bodyVisibilityState.value = View.GONE
        errorVisibilityState.value = View.VISIBLE

        errorState.value = ErrorMessage(errorBodyResource = errorMessage, errorDrawableResource = errorDrawable)
    }

    //endregion

}