package com.mahmoud.nearbyandroid.data.retrofit.services

import com.mahmoud.nearbyandroid.data.models.ResponseFromServer
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesService {

    @GET("venues/explore")
    fun getPlaces(@Query("ll") latLong: String,
                  @Query("client_id") clientId: String,
                  @Query("client_secret") clientSecret: String,
                  @Query("v") versionDate: String) : Call<ResponseFromServer>


}