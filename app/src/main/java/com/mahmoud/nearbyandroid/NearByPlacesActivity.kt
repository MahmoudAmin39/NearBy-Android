package com.mahmoud.nearbyandroid

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.mahmoud.nearbyandroid.viewmodels.NearByPlacesViewModel

class NearByPlacesActivity : AppCompatActivity(), OnSuccessListener<Location> {

    private val viewModel by lazy {
        ViewModelProviders.of(this)[NearByPlacesViewModel::class.java]
    }

    private val locationProvider by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    companion object {
        const val PERMISSION_LOCATION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Runtime permission check
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_LOCATION)
        } else {
            // Permission has already been granted
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted
                    getLocation()
                } else {
                    // permission denied
                }
            }
        }
    }

    private fun getLocation() {
        locationProvider.lastLocation.addOnSuccessListener(this)
    }

    override fun onSuccess(location: Location?) {
        location?.let { viewModel.setLocationSentToServer(it) }
    }
}
