package com.mahmoud.nearbyandroid.helpers

import android.content.Context
import android.location.LocationManager
import com.mahmoud.nearbyandroid.NearByApp

class LocationInfoProvider {

    private val locationManager by lazy {
        NearByApp.appContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    fun isLocationEnabled() : Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}