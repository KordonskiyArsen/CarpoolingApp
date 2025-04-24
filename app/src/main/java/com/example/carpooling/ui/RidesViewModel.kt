package com.example.carpooling.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpooling.data.AppDatabase
import com.example.carpooling.data.Ride
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RidesViewModel(application: Application) : AndroidViewModel(application) {
    private val rideDao = AppDatabase.getDatabase(application).rideDao()
    val rides: StateFlow<List<Ride>> = rideDao.getAllRides()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addRide(ride: Ride) {
        viewModelScope.launch {
            rideDao.insertRide(ride)
        }
    }
}