package com.mahmoud.nearbyandroid.helpers

import android.content.Context
import android.location.LocationManager
import com.mahmoud.nearbyandroid.NearByApp

class LocationInformation {

    private var locationManager : LocationManager? = NearByApp.appContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun isGPSAvailable() : Boolean = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
}