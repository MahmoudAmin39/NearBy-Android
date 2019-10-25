package com.mahmoud.nearbyandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.mahmoud.nearbyandroid.viewmodels.NearByPlacesViewModel

class NearByPlacesActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProviders.of(this)[NearByPlacesViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
