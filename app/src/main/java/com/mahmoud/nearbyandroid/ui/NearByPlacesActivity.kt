package com.mahmoud.nearbyandroid.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.mahmoud.nearbyandroid.viewmodels.NearByPlacesViewModel
import kotlinx.android.synthetic.main.activity_main.*
import android.content.IntentSender
import android.os.Looper
import android.view.Menu
import com.mahmoud.nearbyandroid.R

class NearByPlacesActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private val viewModel by lazy {
        ViewModelProviders.of(this)[NearByPlacesViewModel::class.java]
    }

    private val googleApiClient by lazy {
        GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
    }

    private val locationRequest by lazy {
        LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(LOCATION_UPDATE_INTERVAL)
            .setFastestInterval(1000)
    }

    private val locationProvider by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val locationCallback by lazy {
        object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                // According to docs
                // The array of locations are sorted from oldest to newest
                val latestLocationIndex = locationResult.locations.size - 1
                val location = locationResult.locations[latestLocationIndex]
                viewModel.setCurrentUserLocation(location)
            }
        }
    }

    private lateinit var menu: Menu

    companion object {
        const val PERMISSION_LOCATION = 1
        const val CONNECTION_FAILURE_RESOLUTION_REQUEST = 2
        // The lower the interval, the better the accuracy
        const val LOCATION_UPDATE_INTERVAL: Long = 1000 * 10/** 60 * 5*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        with(viewModel) {
            progressVisibilityState.observe(this@NearByPlacesActivity, Observer { state -> progressBar.visibility = state })
            errorVisibilityState.observe(this@NearByPlacesActivity, Observer { state -> errorView.visibility = state })
            placesListVisibilityState.observe(this@NearByPlacesActivity, Observer { state -> recyclerView_places.visibility = state })

            // Observe data


            // Error object to show
            errorState.observe(this@NearByPlacesActivity, Observer { error -> error?.let {errorObject ->
                imageView_error.setImageResource(errorObject.errorDrawableResource)
                textView_error.text = resources.getString(errorObject.errorBodyResource)
            } })

            // Broadcast Listening
            shouldReceiveLocationBroadCasts.observe(this@NearByPlacesActivity, Observer { should ->
                if (should) {
                    // TODO: Start Receiving Location changes
                }
            })
            shouldReceiveNetworkBroadCasts.observe(this@NearByPlacesActivity, Observer { should ->
                if (should) {
                    // TODO: Start Receiving Networking changes
                }
            })
            shouldStartLocationUpdate.observe(this@NearByPlacesActivity, Observer { should ->
                if (should) {
                    getLocationUpdates()
                }
            })
        }
    }

    private fun getLocation() {
        locationProvider.lastLocation.addOnSuccessListener { location -> viewModel.getPlaces(location)}
    }

    private fun getLocationUpdates() {
        locationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    // region Activity lifecycle callbacks

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted
                    getLocation()
                } else {
                    // permission denied
                    // TODO: Send the view model that permission was denied
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        googleApiClient.connect()
    }

    override fun onStop() {
        if (googleApiClient.isConnected) {
            googleApiClient.disconnect()
        }

        if (locationCallback != null) {
            locationProvider.removeLocationUpdates(locationCallback)
        }
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            this.menu = it
        }
        return true
    }

    // endregion

    // region GoogleApiClient implement

    override fun onConnected(p0: Bundle?) {
        // Runtime permission check
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_LOCATION
            )
        } else {
            // Permission has already been granted
            getLocation()
        }
    }

    override fun onConnectionSuspended(p0: Int) {}

    override fun onConnectionFailed(p0: ConnectionResult) {
        if (p0.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                p0.startResolutionForResult(this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST
                )
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            }
        } else {
            // TODO: Send the view model that Failed connection with Google APIs
        }
    }

    // endregion

}
