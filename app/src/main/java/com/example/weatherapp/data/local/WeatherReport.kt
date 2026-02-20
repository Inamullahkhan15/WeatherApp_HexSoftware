package com.example.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_reports")
data class WeatherReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cityName: String,
    val status: String, // e.g., "Raining", "Sunny", "Very Windy"
    val timestamp: Long = System.currentTimeMillis()
)
