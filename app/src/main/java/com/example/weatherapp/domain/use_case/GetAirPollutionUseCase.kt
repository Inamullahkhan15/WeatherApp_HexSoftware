package com.example.weatherapp.domain.use_case

import com.example.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetAirPollutionUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Int {
        return try {
            repository.getAirPollution(lat, lon)
        } catch (e: Exception) {
            1 // Default to 'Good' if error
        }
    }
}
