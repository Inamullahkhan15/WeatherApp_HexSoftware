package com.example.weatherapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AirPollutionDto(
    @SerializedName("list") val list: List<AirPollutionItemDto>
)

data class AirPollutionItemDto(
    @SerializedName("main") val main: AirPollutionMainDto,
    @SerializedName("components") val components: Map<String, Double>,
    @SerializedName("dt") val dt: Long
)

data class AirPollutionMainDto(
    @SerializedName("aqi") val aqi: Int // Air Quality Index: 1=Good, 2=Fair, 3=Moderate, 4=Poor, 5=Very Poor
)
