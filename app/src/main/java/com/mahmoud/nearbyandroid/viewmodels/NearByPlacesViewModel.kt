package com.mahmoud.nearbyandroid.viewmodels

import android.location.Location
import androidx.lifecycle.ViewModel
import com.mahmoud.nearbyandroid.helpers.LocationInfoProvider
import com.mahmoud.nearbyandroid.helpers.NetworkInformation

class NearByPlacesViewModel : ViewModel() {

    private var locationSentToServer: Location? = null

    private val networkInformation = NetworkInformation()
    private val locationInfoProvider = LocationInfoProvider()

    fun setLocationSentToServer(location: Location?) {
        this.locationSentToServer = location
    }


}