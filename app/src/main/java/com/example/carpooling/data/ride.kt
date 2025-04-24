package com.example.carpooling.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rides")
data class Ride(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fromLocation: String,
    val toLocation: String,
    val dateTime: String,
    val availableSeats: Int,
    val price: Double,
    val comment: String = "",
    val creatorId: Int,
    val bookedSeats: Int = 0
) {
    fun hasAvailableSeats(): Boolean = bookedSeats < availableSeats
}