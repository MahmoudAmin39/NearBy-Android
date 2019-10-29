package com.mahmoud.nearbyandroid.viewmodels

import android.os.AsyncTask
import com.mahmoud.nearbyandroid.data.models.venues.Venue
import com.mahmoud.nearbyandroid.data.room.RoomClient

class RoomAsyncTask(private val callback: VenuesWithPhotosCallback) :
    AsyncTask<List<Venue>, Void, List<Venue>>() {

    override fun doInBackground(vararg venues: List<Venue>): List<Venue> {
        val venuesWithPhotos = ArrayList<Venue>()
        venues.first().map { venue ->
            venue.photoUrl = RoomClient.getInstance().databaseInstance?.photoUrlDao()
                ?.getPhotoUrl(venueId = venue.id!!)
            venuesWithPhotos.add(venue)
        }
        return venuesWithPhotos
    }

    override fun onPostExecute(venues: List<Venue>) {
        callback.onVenuesWithPhotosReady(venues)
    }
}

interface VenuesWithPhotosCallback {
    fun onVenuesWithPhotosReady(venues: List<Venue>)
}