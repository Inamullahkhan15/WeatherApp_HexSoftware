package com.example.weatherapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.use_case.GetForecastUseCase
import com.example.weatherapp.domain.use_case.GetWeatherUseCase
import com.example.weatherapp.utils.LocationTracker
import com.example.weatherapp.utils.Resource
import com.example.weatherapp.data.local.WeatherDao
import com.example.weatherapp.data.local.WeatherReport
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * WeatherViewModel.kt
 *
 * Manages weather data retrieval, processing, and UI state for the home screen.
 * Handles real-time weather updates, unit conversion, forecasts, and community reports
 * using Kotlin Flow and Coroutines for reactive, non-blocking operations.
 *
 * Responsibilities:
 * - Fetch weather and forecast data from the domain layer (use cases)
 * - Manage and expose UI state through [WeatherState]
 * - Handle location-based weather retrieval
 * - Manage community weather reports from local database
 * - Convert between metric and imperial units
 *
 * @author Weather App Team
 * @version 1.0
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val getAirPollutionUseCase: com.example.weatherapp.domain.use_case.GetAirPollutionUseCase,
    private val locationTracker: LocationTracker,
    private val dao: WeatherDao
) : ViewModel() {

    // Mutable state that UI observes for updates
    private val _state = mutableStateOf(WeatherState())
    val state: State<WeatherState> = _state
    
    // StateFlow for managing community weather reports for a specific city
    // Uses flatMapLatest to ensure previous subscriptions are cancelled when city changes
    private val _currentCity = MutableStateFlow("")
    val cityReports: StateFlow<List<WeatherReport>> = _currentCity
        .flatMapLatest { city ->
            if (city.isEmpty()) flowOf(emptyList())
            else dao.getReportsForCity(city)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadWeatherInfo()
    }

    /**
     * Toggles between metric (°C) and imperial (°F) units and reloads weather data.
     * Ensures all displayed values are recalculated with new units.
     */
    fun toggleUnits() {
        val newUnits = if (state.value.units == "metric") "imperial" else "metric"
        _state.value = state.value.copy(units = newUnits)
        loadWeatherInfo()
    }

    /**
     * Initiates weather data loading based on device location or defaults to London.
     * Sets loading state and attempts to get current location using FusedLocationProvider.
     * Falls back to London if location services are unavailable.
     */
    fun loadWeatherInfo() {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true, error = "")
            val units = state.value.units
            
            // Attempt to retrieve device's current location
            val location = locationTracker.getCurrentLocation()
            if (location != null) {
                getWeatherByLocation(location.latitude, location.longitude, units)
            } else {
                // Fallback to default city if location is unavailable
                getWeather("London", units)
            }
        }
    }

    /**
     * Fetches weather data for a given city name.
     * Updates state with weather info, forecast, and air quality data.
     *
     * @param city The city name to fetch weather for
     * @param units Temperature units: "metric" (°C) or "imperial" (°F)
     */
    fun getWeather(city: String, units: String = state.value.units) {
        getWeatherUseCase(city, units).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = state.value.copy(
                        weather = result.data,
                        isLoading = false,
                        error = ""
                    )
                    updateCityReports(city)
                    getForecast(city, units)
                    
                    // Fetch Air Quality Index using coordinates from weather response
                    result.data?.let { w ->
                        getAirPollutionForCity(w.lat, w.lon)
                    }
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(
                        error = result.message ?: "An unknown error occurred",
                        isLoading = false
                    )
                }
                is Resource.Loading -> {
                    _state.value = state.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Fetches 5-day weather forecast for a given city.
     * Processes the 3-hourly forecast data into hourly (next 24h) and daily formats.
     *
     * @param city The city name
     * @param units Temperature units for forecast data
     */
    private fun getForecast(city: String, units: String = state.value.units) {
        getForecastUseCase(city, units).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val allItems = result.data ?: emptyList()
                    // Take first 8 items for next 24 hours (8 * 3 hours = 24 hours)
                    _state.value = state.value.copy(
                        hourlyForecast = allItems.take(8),
                        dailyForecast = groupToDailyForecast(allItems)
                    )
                }
                is Resource.Error -> { }
                is Resource.Loading -> { }
            }
        }.launchIn(viewModelScope)
    }
    
    /**
     * Fetches weather data based on GPS coordinates (latitude, longitude).
     * Suitable for device's current location.
     *
     * @param lat Latitude coordinate
     * @param lon Longitude coordinate
     * @param units Temperature units ("metric" or "imperial")
     */
    fun getWeatherByLocation(lat: Double, lon: Double, units: String = state.value.units) {
        viewModelScope.launch {
            val aqi = getAirPollutionUseCase(lat, lon)
            _state.value = state.value.copy(aqi = aqi)
        }
        
        getWeatherUseCase(lat, lon, units).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = state.value.copy(
                        weather = result.data,
                        isLoading = false,
                        error = ""
                    )
                    result.data?.cityName?.let { updateCityReports(it) }
                    getForecastByLocation(lat, lon, units)
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(
                        error = result.message ?: "An unknown error occurred",
                        isLoading = false
                    )
                }
                is Resource.Loading -> {
                    _state.value = state.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Fetches forecast data using geographic coordinates.
     * Similar to [getForecast] but uses latitude/longitude instead of city name.
     */
    private fun getForecastByLocation(lat: Double, lon: Double, units: String = state.value.units) {
        getForecastUseCase(lat, lon, units).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val allItems = result.data ?: emptyList()
                    _state.value = state.value.copy(
                        hourlyForecast = allItems.take(8),
                        dailyForecast = groupToDailyForecast(allItems)
                    )
                }
                is Resource.Error -> { }
                is Resource.Loading -> { }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Fetches Air Quality Index (AQI) data for given coordinates.
     * AQI indicates pollution levels: 1 (Good) to 5 (Very Poor).
     * Used for health and wellness alerts.
     *
     * @param lat Latitude coordinate
     * @param lon Longitude coordinate
     */
    private fun getAirPollutionForCity(lat: Double, lon: Double) {
        viewModelScope.launch {
            val aqi = getAirPollutionUseCase(lat, lon)
            _state.value = state.value.copy(aqi = aqi)
        }
    }

    /**
     * Submits a user-reported weather observation to the local database.
     * Used for community-driven weather reporting feature.
     *
     * @param cityName The city where the observation was made
     * @param status The observed weather status (e.g., "Raining", "Sunny", "Cloudy")
     */
    fun submitReport(cityName: String, status: String) {
        viewModelScope.launch {
            dao.insertReport(WeatherReport(cityName = cityName, status = status))
        }
    }

    /**
     * Updates the current city filter for retrieving community reports.
     * Triggers Flow to re-query database for reports in the new city.
     *
     * @param cityName The new city to filter reports by
     */
    private fun updateCityReports(cityName: String) {
        _currentCity.value = cityName
    }

    /**
     * Converts 3-hourly forecast items into daily forecast summaries.
     * Groups forecast items by date, calculates min/max temperatures, and determines
     * the most frequent weather condition for each day.
     *
     * @param items List of hourly forecast items (3-hour intervals)
     * @return List of daily forecasts with aggregated weather data
     */
    private fun groupToDailyForecast(items: List<com.example.weatherapp.domain.model.ForecastItem>): List<com.example.weatherapp.domain.model.DayForecast> {
        return items.groupBy { it.dateTime.split(" ")[0] }
            .map { (date, dailyItems) ->
                val maxTemp = dailyItems.maxOf { it.maxTemp }
                val minTemp = dailyItems.minOf { it.minTemp }
                val mostFrequentIcon = dailyItems.groupBy { it.icon }.maxBy { it.value.size }.key
                
                // Convert date string to readable day name (e.g., "Mon", "Today")
                val dayName = try {
                    val localDate = java.time.LocalDate.parse(date)
                    if (localDate == java.time.LocalDate.now()) "Today"
                    else localDate.format(java.time.format.DateTimeFormatter.ofPattern("EEE"))
                } catch (e: Exception) {
                    date
                }

                com.example.weatherapp.domain.model.DayForecast(
                    dayName = dayName,
                    date = date,
                    icon = mostFrequentIcon,
                    minTemp = minTemp,
                    maxTemp = maxTemp,
                    description = dailyItems[0].description
                )
            }
    }
}
