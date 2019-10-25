package com.mahmoud.nearbyandroid

import android.content.Context
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private val locationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val locationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }
}
