package com.example.weatherapp.utils

object UnitConverter {
    fun toCelsius(temp: Double, units: String): Double {
        return if (units == "imperial") {
            (temp - 32) * 5 / 9
        } else {
            temp
        }
    }
}
