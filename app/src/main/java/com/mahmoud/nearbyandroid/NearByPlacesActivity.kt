package com.mahmoud.nearbyandroid

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.mahmoud.nearbyandroid.viewmodels.NearByPlacesViewModel
import kotlinx.android.synthetic.main.activity_main.*

class NearByPlacesActivity : AppCompatActivity(), OnSuccessListener<Location> {

    private val viewModel by lazy {
        ViewModelProviders.of(this)[NearByPlacesViewModel::class.java]
    }

    private val alertDialogBuilder by lazy {
        AlertDialog.Builder(this)
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

        with(viewModel) {
            progressVisibilityState.observe(this@NearByPlacesActivity, Observer { state -> progressBar.visibility = state })
            errorVisibilityState.observe(this@NearByPlacesActivity, Observer { state -> errorView.visibility = state })
            bodyVisibilityState.observe(this@NearByPlacesActivity, Observer { state -> recyclerView_places.visibility = state })

            // Error object to show
            errorState.observe(this@NearByPlacesActivity, Observer { error -> error?.let {errorObject ->
                imageView_error.setImageResource(errorObject.errorDrawableResource)
                textView_error.text = resources.getString(errorObject.errorBodyResource)
            } })

            // Alert to show
            alertMessage.observe(this@NearByPlacesActivity, Observer { alertMessage ->
                when(alertMessage) {
                    0 -> return@Observer
                    else -> {
                        with(alertDialogBuilder) {
                            title = getString(R.string.recommendation)
                            setMessage(getString(alertMessage))
                            setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
                            show()
                        }
                    }
                }
            })

            // Broadcast Listening
            shouldReceiveLocationBroadCasts.observe(this@NearByPlacesActivity, Observer { should -> })
            shouldReceiveNetworkBroadCasts.observe(this@NearByPlacesActivity, Observer { should -> })
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
        viewModel.setLocationSentToServer(location)
    }
}
