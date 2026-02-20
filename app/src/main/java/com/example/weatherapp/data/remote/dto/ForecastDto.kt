package com.example.weatherapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * ForecastDto
 * purpose: Represents the JSON response for the 5-day weather forecast.
 * It contains a list of weather updates every 3 hours.
 */
data class ForecastDto(
    @SerializedName("list") val list: List<ForecastItemDto>,
    @SerializedName("city") val city: CityDto
)

data class ForecastItemDto(
    @SerializedName("dt") val dt: Long,
    @SerializedName("main") val main: MainDto,
    @SerializedName("weather") val weather: List<WeatherDescriptionDto>,
    @SerializedName("wind") val wind: WindDto,
    @SerializedName("dt_txt") val dtTxt: String
)

data class CityDto(
    @SerializedName("name") val name: String,
    @SerializedName("coord") val coord: CoordDto,
    @SerializedName("country") val country: String
)
