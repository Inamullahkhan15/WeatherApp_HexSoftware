package com.example.weatherapp.domain.model

/**
 * Weather (Domain Model)
 * purpose: A clean, simple model to be used by the UI. 
 * We isolate our UI from the API structure so that if the API changes, 
 * we only update the mapping, not the whole UI.
 */
data class Weather(
    val cityName: String,
    val temperature: Double,
    val description: String,
    val icon: String,
    val humidity: Int,
    val windSpeed: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val condition: String,
    val timestamp: Long,
    val pressure: Int = 0,
    val visibility: Int = 0,
    val sunrise: Long = 0,
    val sunset: Long = 0,
    val lat: Double = 0.0,
    val lon: Double = 0.0
)
