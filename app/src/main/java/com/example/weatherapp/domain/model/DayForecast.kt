package com.example.weatherapp.domain.model

/**
 * DayForecast (Domain Model)
 * purpose: Represents a single day's summary for the 5-day forecast list.
 */
data class DayForecast(
    val dayName: String,
    val date: String,
    val icon: String,
    val minTemp: Double,
    val maxTemp: Double,
    val description: String
)
