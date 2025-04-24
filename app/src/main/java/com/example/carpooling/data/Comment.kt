package com.example.carpooling.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rideId: Int,
    val userId: Int,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)