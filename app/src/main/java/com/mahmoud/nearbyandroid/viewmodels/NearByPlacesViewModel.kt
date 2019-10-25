package com.mahmoud.nearbyandroid.viewmodels

import androidx.lifecycle.ViewModel
import com.mahmoud.nearbyandroid.helpers.LocationInfoProvider
import com.mahmoud.nearbyandroid.helpers.NetworkInformation

class NearByPlacesViewModel : ViewModel() {

    private val networkInformation = NetworkInformation()
    private val locationInfoProvider = LocationInfoProvider()
}