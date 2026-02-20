package com.example.weatherapp.domain.model

/**
 * ForecastItem (Domain Model)
 * purpose: Represents a single entry in the 5-day forecast.
 */
data class ForecastItem(
    val dateTime: String,
    val temp: Double,
    val description: String,
    val icon: String,
    val minTemp: Double,
    val maxTemp: Double,
    val humidity: Int,
    val windSpeed: Double
)
