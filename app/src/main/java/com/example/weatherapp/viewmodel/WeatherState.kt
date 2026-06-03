package com.example.weatherapp.viewmodel

import com.example.weatherapp.domain.model.DayForecast
import com.example.weatherapp.domain.model.ForecastItem
import com.example.weatherapp.domain.model.Weather

/**
 * WeatherState.kt
 *
 * Immutable data class representing the complete UI state for the home screen.
 * Using a single state object makes state management predictable and testable.
 * The UI recomposes whenever any property changes due to Jetpack Compose's state tracking.
 *
 * Properties:
 * - isLoading: Indicates data fetch is in progress
 * - weather: Current weather data (null if not fetched)
 * - hourlyForecast: Weather forecast for next 24 hours (3-hour intervals)
 * - dailyForecast: Weather forecast for next 5 days (daily summaries)
 * - aqi: Air Quality Index (1-5 scale) for pollution monitoring
 * - units: Temperature unit system ("metric" for °C, "imperial" for °F)
 * - error: Error message if data fetch fails (empty if successful)
 *
 * @author Weather App Team
 * @version 1.0
 */
data class WeatherState(
    // UI loading indicator
    val isLoading: Boolean = false,
    
    // Current weather data for the selected location
    val weather: Weather? = null,
    
    // Hourly forecast items (3-hour intervals for next 24 hours)
    val hourlyForecast: List<ForecastItem> = emptyList(),
    
    // Aggregated daily forecast for next 5 days
    val dailyForecast: List<DayForecast> = emptyList(),
    
    // Air Quality Index: 1=Good, 2=Fair, 3=Moderate, 4=Poor, 5=Very Poor
    val aqi: Int = 1,
    
    // Unit system for temperature display and calculations
    val units: String = "metric",
    
    // Error message for UI to display to user
    val error: String = ""
)
