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
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.mahmoud.nearbyandroid.R
import com.mahmoud.nearbyandroid.data.Constants.Companion.APPMODE_REALTIME
import com.mahmoud.nearbyandroid.data.Constants.Companion.APPMODE_SINGLE_UPDATE
import com.mahmoud.nearbyandroid.data.Constants.Companion.ERROR_GOOGLE_PLAY_CONNECTION_FAILED
import com.mahmoud.nearbyandroid.data.Constants.Companion.ERROR_NO_INTERNET
import com.mahmoud.nearbyandroid.data.Constants.Companion.ERROR_NO_LOCATION
import com.mahmoud.nearbyandroid.data.Constants.Companion.ERROR_NO_RESPONSE
import com.mahmoud.nearbyandroid.data.Constants.Companion.ERROR_PERMISSION_DENIED

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

    private var menu: Menu? = null
    private lateinit var adapter: VenuesAdapter

    companion object {
        const val PERMISSION_LOCATION = 1
        const val CONNECTION_FAILURE_RESOLUTION_REQUEST = 2
        // The lower the interval, the better the accuracy
        const val LOCATION_UPDATE_INTERVAL: Long = 1000 * 60/** 60 * 5*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = VenuesAdapter(this, ArrayList())
        recyclerView_places.layoutManager = LinearLayoutManager(this)
        recyclerView_places.adapter = adapter

        with(viewModel) {
            // Observe the visibility manipulation
            progressVisibilityState.observe(this@NearByPlacesActivity, Observer { state -> progressBar.visibility = state })
            errorVisibilityState.observe(this@NearByPlacesActivity, Observer { state -> errorView.visibility = state })
            placesListVisibilityState.observe(this@NearByPlacesActivity, Observer { state -> recyclerView_places.visibility = state })

            appMode.observe(this@NearByPlacesActivity, Observer { mode ->
                mode?.let {
                    when (mode) {
                        APPMODE_REALTIME -> {
                            menu?.findItem(R.id.menu_item)?.title = getString(R.string.single_update)
                        }
                        APPMODE_SINGLE_UPDATE -> {
                            menu?.findItem(R.id.menu_item)?.title = getString(R.string.realtime)
                            removeLocationUpdates()
                        }
                    }
                    checkPermission(it)
                }
            })

            // Observe data
            venuesData.observe(this@NearByPlacesActivity, Observer { venues -> adapter.addVenues(venues) })

            // Error object to show
            errorState.observe(this@NearByPlacesActivity, Observer { error -> error?.let {errorObject ->
                imageView_error.setImageResource(errorObject.errorDrawableResource)
                textView_error.text = resources.getString(errorObject.errorBodyResource)
                when(errorObject.errorCode) {
                    ERROR_NO_LOCATION, ERROR_NO_INTERNET -> button_error.setOnClickListener { viewModel.checkConditions() }
                    ERROR_NO_RESPONSE -> button_error.setOnClickListener { loadPlaces() }
                    ERROR_PERMISSION_DENIED -> button_error.setOnClickListener { checkPermission(viewModel.appMode.value) }
                    ERROR_GOOGLE_PLAY_CONNECTION_FAILED -> button_error.setOnClickListener { googleApiClient.connect() }
                }
            } })
        }
    }

    private fun getLocation() {
        locationProvider.lastLocation.addOnSuccessListener { location -> viewModel.setCurrentUserLocation(location)}
    }

    private fun getLocationUpdates() {
        getLocation()
        locationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    // region Activity lifecycle callbacks

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted
                    viewModel.checkConditions()
                } else {
                    // permission denied
                    viewModel.onPermissionDenied()
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
        removeLocationUpdates()
        super.onStop()
    }

    private fun removeLocationUpdates() {
        if (locationCallback != null) {
            locationProvider.removeLocationUpdates(locationCallback)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.let { this.menu = it }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.onMenuItemClicked()
        return true
    }

    // endregion

    // region GoogleApiClient implement

    override fun onConnected(p0: Bundle?) {
        viewModel.checkConditions()
    }

    private fun checkPermission(appMode: Int?) {
        // Runtime permission check
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_LOCATION
            )
        } else {
            // Permission has already been granted
            appMode?.let {
                when(it) {
                    APPMODE_REALTIME -> getLocationUpdates()
                    APPMODE_SINGLE_UPDATE -> getLocation()
                }
            }
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        viewModel.onConnectionToGooglePlayFailed()
    }

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
            viewModel.onConnectionToGooglePlayFailed()
        }
    }

    // endregion

}
