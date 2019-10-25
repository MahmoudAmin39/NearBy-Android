package com.mahmoud.nearbyandroid.helpers

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.mahmoud.nearbyandroid.NearByApp

class NetworkInformation {

    private var networkInfo : NetworkInfo?

    init {
        val connectivityManager = NearByApp.appContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkInfo = connectivityManager.activeNetworkInfo
    }

    fun isInternetConnected() : Boolean = networkInfo?.isConnected ?: false
}