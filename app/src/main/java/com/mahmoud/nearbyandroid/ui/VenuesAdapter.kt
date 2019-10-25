package com.mahmoud.nearbyandroid.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahmoud.nearbyandroid.R
import com.mahmoud.nearbyandroid.data.models.Venue

class VenuesAdapter(private val venues: ArrayList<Venue>) : RecyclerView.Adapter<VenuesAdapter.VenueViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return VenueViewHolder(view)
    }

    override fun getItemCount(): Int {
        return venues.size
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        holder.bindViews(venues[position])
    }


    inner class VenueViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var venueImage: ImageView = v.findViewById(R.id.imageView_place)
        private var venueName: TextView = v.findViewById(R.id.textView_venue_name)
        private var venueAddress: TextView = v.findViewById(R.id.textView_venue_address)

        fun bindViews(venue: Venue) {
            venueName.text = venue.name ?: "Venue name here"
            venueAddress.text = venue.location ?: "Venue address here"
        }
    }
}