package com.example.weatherapp.domain.repository

import com.example.weatherapp.domain.model.ForecastItem
import com.example.weatherapp.domain.model.Weather

interface WeatherRepository {
    suspend fun getCurrentWeather(city: String, units: String): Weather
    suspend fun getCurrentWeatherByLocation(lat: Double, lon: Double, units: String): Weather
    suspend fun getForecast(city: String, units: String): List<ForecastItem>
    suspend fun getForecastByLocation(lat: Double, lon: Double, units: String): List<ForecastItem>
    suspend fun getAirPollution(lat: Double, lon: Double): Int
}
