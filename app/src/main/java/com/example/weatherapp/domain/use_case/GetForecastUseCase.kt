package com.example.weatherapp.domain.use_case

import com.example.weatherapp.domain.model.ForecastItem
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * GetForecastUseCase
 * purpose: Business logic for fetching the 5-day weather forecast.
 */
class GetForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(city: String, units: String = "metric"): Flow<Resource<List<ForecastItem>>> = flow {
        try {
            emit(Resource.Loading())
            val forecast = repository.getForecast(city, units)
            emit(Resource.Success(forecast))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    operator fun invoke(lat: Double, lon: Double, units: String = "metric"): Flow<Resource<List<ForecastItem>>> = flow {
        try {
            emit(Resource.Loading())
            val forecast = repository.getForecastByLocation(lat, lon, units)
            emit(Resource.Success(forecast))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}
