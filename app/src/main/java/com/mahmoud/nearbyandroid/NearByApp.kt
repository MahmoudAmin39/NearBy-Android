package com.mahmoud.nearbyandroid

import android.app.Application
import android.content.Context

class NearByApp : Application() {

    companion object {
        // Although this context is static but it won't leak memory
        // because it is an application context not an activity context
        var appContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}