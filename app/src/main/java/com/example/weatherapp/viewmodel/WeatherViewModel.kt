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
 * WeatherViewModel
 * purpose: Bridge between the UI and Domain layer. 
 * It triggers use cases and updates the WeatherState which the UI observes.
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

    private val _state = mutableStateOf(WeatherState())
    val state: State<WeatherState> = _state
    
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

    fun toggleUnits() {
        val newUnits = if (state.value.units == "metric") "imperial" else "metric"
        _state.value = state.value.copy(units = newUnits)
        loadWeatherInfo()
    }

    fun loadWeatherInfo() {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true, error = "")
            val units = state.value.units
            
            val location = locationTracker.getCurrentLocation()
            if (location != null) {
                getWeatherByLocation(location.latitude, location.longitude, units)
            } else {
                getWeather("London", units)
            }
        }
    }

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
                    
                    // NEW: Fetch AQI using coordinates from the weather response
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

    private fun getForecast(city: String, units: String = state.value.units) {
        getForecastUseCase(city, units).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val allItems = result.data ?: emptyList()
                    _state.value = state.value.copy(
                        hourlyForecast = allItems.take(8), // Next 24 hours (8 * 3h)
                        dailyForecast = groupToDailyForecast(allItems)
                    )
                }
                is Resource.Error -> { }
                is Resource.Loading -> { }
            }
        }.launchIn(viewModelScope)
    }
    
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

    private fun getAirPollutionForCity(lat: Double, lon: Double) {
        viewModelScope.launch {
            val aqi = getAirPollutionUseCase(lat, lon)
            _state.value = state.value.copy(aqi = aqi)
        }
    }

    fun submitReport(cityName: String, status: String) {
        viewModelScope.launch {
            dao.insertReport(WeatherReport(cityName = cityName, status = status))
        }
    }

    private fun updateCityReports(cityName: String) {
        _currentCity.value = cityName
    }

    private fun groupToDailyForecast(items: List<com.example.weatherapp.domain.model.ForecastItem>): List<com.example.weatherapp.domain.model.DayForecast> {
        return items.groupBy { it.dateTime.split(" ")[0] }
            .map { (date, dailyItems) ->
                val maxTemp = dailyItems.maxOf { it.maxTemp }
                val minTemp = dailyItems.minOf { it.minTemp }
                val mostFrequentIcon = dailyItems.groupBy { it.icon }.maxBy { it.value.size }.key
                
                // Convert date to Day Name (e.g., "Mon")
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
