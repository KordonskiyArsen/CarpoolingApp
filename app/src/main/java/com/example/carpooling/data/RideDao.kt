package com.example.carpooling.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RideDao {
    @Query("SELECT * FROM rides")
    fun getAllRides(): Flow<List<Ride>>

    @Insert
    suspend fun insertRide(ride: Ride)

    @Query("SELECT * FROM rides WHERE id = :rideId LIMIT 1")
    suspend fun getRideById(rideId: Int): Ride?
}