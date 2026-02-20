package com.example.weatherapp.domain.use_case

import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * GetWeatherUseCase
 * purpose: Business logic for fetching current weather.
 * It handles errors (No internet / City not found) and returns a Flow of Resource.
 */
class GetWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(city: String, units: String = "metric"): Flow<Resource<Weather>> = flow {
        try {
            emit(Resource.Loading())
            val weather = repository.getCurrentWeather(city, units)
            emit(Resource.Success(weather))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
    
    operator fun invoke(lat: Double, lon: Double, units: String = "metric"): Flow<Resource<Weather>> = flow {
        try {
            emit(Resource.Loading())
            val weather = repository.getCurrentWeatherByLocation(lat, lon, units)
            emit(Resource.Success(weather))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}
