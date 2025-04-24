package com.example.carpooling.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CommentDao {
    @Insert
    suspend fun insertComment(comment: Comment)

    @Query("SELECT * FROM comments WHERE rideId = :rideId ORDER BY timestamp DESC")
    suspend fun getCommentsForRide(rideId: Int): List<Comment>
}