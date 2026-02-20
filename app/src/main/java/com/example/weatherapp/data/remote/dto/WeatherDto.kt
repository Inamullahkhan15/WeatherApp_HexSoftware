package com.example.weatherapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * WeatherDto
 * purpose: Represents the JSON response for current weather from OpenWeatherMap API.
 * We use @SerializedName to map the JSON keys to our Kotlin variables.
 */
data class WeatherDto(
    @SerializedName("coord") val coord: CoordDto,
    @SerializedName("weather") val weather: List<WeatherDescriptionDto>,
    @SerializedName("main") val main: MainDto,
    @SerializedName("wind") val wind: WindDto,
    @SerializedName("visibility") val visibility: Int,
    @SerializedName("sys") val sys: SysDto,
    @SerializedName("name") val cityName: String,
    @SerializedName("dt") val timestamp: Long
)

data class SysDto(
    @SerializedName("sunrise") val sunrise: Long,
    @SerializedName("sunset") val sunset: Long
)

data class CoordDto(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double
)

data class WeatherDescriptionDto(
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)

data class MainDto(
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("humidity") val humidity: Int
)

data class WindDto(
    @SerializedName("speed") val speed: Double,
    @SerializedName("deg") val deg: Int
)
