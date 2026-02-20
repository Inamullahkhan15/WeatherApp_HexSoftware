package com.example.weatherapp.viewmodel

import com.example.weatherapp.domain.model.DayForecast
import com.example.weatherapp.domain.model.ForecastItem
import com.example.weatherapp.domain.model.Weather

/**
 * WeatherState
 * purpose: Represents the complete UI state for the Home Screen.
 * Using a single state class makes it easy to manage and update the UI.
 */
data class WeatherState(
    val isLoading: Boolean = false,
    val weather: Weather? = null,
    val hourlyForecast: List<ForecastItem> = emptyList(),
    val dailyForecast: List<DayForecast> = emptyList(),
    val aqi: Int = 1,
    val units: String = "metric",
    val error: String = ""
)
