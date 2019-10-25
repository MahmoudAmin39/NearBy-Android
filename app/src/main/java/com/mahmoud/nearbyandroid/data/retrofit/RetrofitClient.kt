package com.mahmoud.nearbyandroid.data.retrofit

import com.mahmoud.nearbyandroid.data.retrofit.services.PlacesService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    private var retrofit: Retrofit? = null
    var placesService: PlacesService?

    init {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("https://api.foursquare.com/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        placesService = retrofit?.create(PlacesService::class.java)
    }

    companion object {
        private var instance: RetrofitClient? = null

        fun getInstance(): RetrofitClient {
            if (instance == null) {
                instance = RetrofitClient()
            }
            return instance as RetrofitClient
        }
    }
}