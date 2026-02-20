package com.example.weatherapp.utils

import android.location.Location

/**
 * LocationTracker Interface
 * purpose: Abstract interface to get the device's current location.
 */
interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
}
