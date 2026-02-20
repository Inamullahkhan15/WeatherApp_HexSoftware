package com.example.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * FavoriteCity Entity
 * purpose: Represents a city that the user has marked as favorite.
 * We store this in a local Room database.
 */
@Entity(tableName = "favorite_cities")
data class FavoriteCity(
    @PrimaryKey val cityName: String,
    val lat: Double,
    val lon: Double,
    val addedTimestamp: Long = System.currentTimeMillis()
)
