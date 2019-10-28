package com.mahmoud.nearbyandroid.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mahmoud.nearbyandroid.R
import com.mahmoud.nearbyandroid.data.models.venues.Venue
import com.mahmoud.nearbyandroid.data.room.RoomClient
import com.mahmoud.nearbyandroid.viewmodels.VenueViewModel

class VenuesAdapter(private val lifecycleOwner: LifecycleOwner, private val venues: ArrayList<Venue>) : RecyclerView.Adapter<VenuesAdapter.VenueViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return VenueViewHolder(view)
    }

    override fun getItemCount(): Int {
        return venues.size
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        holder.bindViews(venues[position], lifecycleOwner)
    }

    fun addVenues(venues: ArrayList<Venue>) {
        this.venues.clear()
        this.venues.addAll(venues)
        notifyDataSetChanged()
    }

    inner class VenueViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var venueImage: ImageView = v.findViewById(R.id.imageView_place)
        private var venueName: TextView = v.findViewById(R.id.textView_venue_name)
        private var venueAddress: TextView = v.findViewById(R.id.textView_venue_address)

        fun bindViews(venue: Venue, lifecycleOwner: LifecycleOwner) {
            venueName.text = venue.name ?: "Venue name here"
            venueAddress.text = venue.location ?: "Venue address here"

            RoomClient.getInstance().databaseInstance?.photoUrlDao()?.getPhotoUrlLiveData(venueId = venue.id!!)?.observe(lifecycleOwner, Observer { imageUrl ->
                imageUrl?.let {
                    if (imageUrl != "") {
                        Glide.with(itemView.context).load(imageUrl).into(venueImage)
                    }
                    return@let
                }
            })

            // Tell the Places view model to get the Photo url
            val venueViewModel = VenueViewModel()
            venueViewModel.getImageUrl(venue.id!!)
        }
    }
}