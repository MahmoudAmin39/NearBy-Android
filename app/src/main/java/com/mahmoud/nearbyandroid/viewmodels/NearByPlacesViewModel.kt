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

    val errorState: MutableLiveData<ErrorMessage?> = MutableLiveData(null)

    val bodyVisbilityState: MutableLiveData<Int> = MutableLiveData(View.GONE)
    val errorVisbilityState: MutableLiveData<Int> = MutableLiveData(View.GONE)
    val progressVisbilityState: MutableLiveData<Int> = MutableLiveData(View.GONE)

    fun setLocationSentToServer(location: Location?) {
        this.lastLocationSentToServer = location
    }

    fun checkLocation() {
        if (!locationInfoProvider.isLocationEnabled()) {
            // Show the Error view
            showError(R.string.app_name, R.drawable.ic_launcher_foreground)
        }
    }

    private fun showBody() {
        progressVisbilityState.value = View.GONE
        bodyVisbilityState.value = View.VISIBLE
        errorVisbilityState.value = View.GONE
    }

    private fun showProgress() {
        progressVisbilityState.value = View.VISIBLE
        bodyVisbilityState.value = View.GONE
        errorVisbilityState.value = View.GONE
    }

    private fun showError(errorMessage: Int, errorDrawable: Int) {
        progressVisbilityState.value = View.GONE
        bodyVisbilityState.value = View.GONE
        errorVisbilityState.value = View.VISIBLE

        errorState.value = ErrorMessage(errorBodyResource = errorMessage, errorDrawableResource = errorDrawable)
    }

}