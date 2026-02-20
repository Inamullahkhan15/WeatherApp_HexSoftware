package com.example.weatherapp.data.repository

import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.remote.dto.AirPollutionDto
import com.example.weatherapp.data.remote.dto.toForecastList
import com.example.weatherapp.data.remote.dto.toWeather
import com.example.weatherapp.domain.model.ForecastItem
import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
) : WeatherRepository {

    override suspend fun getCurrentWeather(city: String, units: String): Weather {
        return api.getCurrentWeather(city, units = units).toWeather()
    }

    override suspend fun getCurrentWeatherByLocation(lat: Double, lon: Double, units: String): Weather {
        return api.getCurrentWeatherByLocation(lat, lon, units = units).toWeather()
    }

    override suspend fun getForecast(city: String, units: String): List<ForecastItem> {
        return api.getForecast(city, units = units).toForecastList()
    }

    override suspend fun getForecastByLocation(lat: Double, lon: Double, units: String): List<ForecastItem> {
        return api.getForecastByLocation(lat, lon, units = units).toForecastList()
    }

    override suspend fun getAirPollution(lat: Double, lon: Double): Int {
        return api.getAirPollution(lat, lon).list[0].main.aqi
    }
}
