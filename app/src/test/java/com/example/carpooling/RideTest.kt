package com.example.carpooling// com.example.carpooling.RideTest.kt
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import com.example.carpooling.data.Ride
class RideTest {

    @Test
    fun hasAvailableSeats_shouldReturnTrue_whenSeatsAvailable() {
        val ride = Ride(
            id = 1,
            fromLocation = "Kyiv",
            toLocation = "Lviv",
            dateTime = "25.04.2025",
            availableSeats = 3,
            price = 300.0,
            creatorId = 5,
            bookedSeats = 2
        )
        assertTrue(ride.hasAvailableSeats())
    }

    @Test
    fun hasAvailableSeats_shouldReturnFalse_whenNoSeatsAvailable() {
        val ride = Ride(
            id = 2,
            fromLocation = "Odessa",
            toLocation = "Kharkiv",
            dateTime = "25.04.2025",
            availableSeats = 2,
            price = 400.0,
            creatorId = 8,
            bookedSeats = 2
        )
        assertFalse(ride.hasAvailableSeats())
    }
}