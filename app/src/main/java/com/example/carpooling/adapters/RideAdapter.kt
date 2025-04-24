package com.example.carpooling.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carpooling.R
import com.example.carpooling.data.Ride

class RideAdapter(
    private val rideList: List<Ride>,
    private val onItemClick: (Ride) -> Unit
) : RecyclerView.Adapter<RideAdapter.RideViewHolder>() {

    class RideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fromTo: TextView = itemView.findViewById(R.id.textViewFromTo)
        val dateTime: TextView = itemView.findViewById(R.id.textViewDateTime)
        val seatsPrice: TextView = itemView.findViewById(R.id.textViewSeatsPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ride, parent, false)
        return RideViewHolder(view)
    }

    override fun onBindViewHolder(holder: RideViewHolder, position: Int) {
        val ride = rideList[position]
        holder.fromTo.text = "${ride.fromLocation} → ${ride.toLocation}"
        holder.dateTime.text = ride.dateTime
        holder.seatsPrice.text = "Місця: ${ride.availableSeats}, Ціна: ${ride.price}₴"
        holder.itemView.setOnClickListener { onItemClick(ride) }
    }

    override fun getItemCount(): Int = rideList.size
}