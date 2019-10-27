package com.mahmoud.nearbyandroid.ui

import android.util.Log
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
import com.mahmoud.nearbyandroid.viewmodels.PlaceViewModel

class VenuesAdapter(private val lifecycleOwner: LifecycleOwner, private val venues: ArrayList<Venue>) : RecyclerView.Adapter<VenuesAdapter.VenueViewHolder>() {

    private var indicesThatSentRequests = mutableListOf<Int>()

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

        private val viewModel = PlaceViewModel()

        fun bindViews(venue: Venue, lifecycleOwner: LifecycleOwner) {
            venueName.text = venue.name ?: "Venue name here"
            venueAddress.text = venue.location ?: "Venue address here"

            // Sometimes the scrolling was not smooth so moved the the request to a new runnable
            venueImage.post { viewModel.getImageUrl(venue.id!!) }

            viewModel.imageUrl.observe(lifecycleOwner, Observer { imageUrl ->
                if (imageUrl != "") {
                    Glide.with(itemView.context).load(imageUrl).into(venueImage)
                }
            })

            viewModel.errorIcon.observe(lifecycleOwner, Observer{ errorIcon ->
                if (errorIcon != 0) {
                    venueImage.setImageResource(errorIcon)
                }
            })

            // For testing
            viewModel.errorText.observe(lifecycleOwner, Observer { errorMessage ->
                if (errorMessage != "") {
                    Log.e("Photo error", errorMessage)
                }
            })
        }
    }
}