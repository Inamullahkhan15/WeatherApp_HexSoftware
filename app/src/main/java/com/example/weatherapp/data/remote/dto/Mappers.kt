package com.example.weatherapp.data.remote.dto

import com.example.weatherapp.domain.model.DayForecast
import com.example.weatherapp.domain.model.ForecastItem
import com.example.weatherapp.domain.model.Weather
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Mapper Functions
 * purpose: Converts API data objects (DTOs) into clean Domain models.
 * This keeps our internal app logic separate from the API's JSON structure.
 */

fun WeatherDto.toWeather(): Weather {
    return Weather(
        cityName = cityName,
        temperature = main.temp,
        description = weather[0].description,
        icon = weather[0].icon,
        humidity = main.humidity,
        windSpeed = wind.speed,
        feelsLike = main.feelsLike,
        tempMin = main.tempMin,
        tempMax = main.tempMax,
        condition = weather[0].main,
        timestamp = timestamp,
        pressure = main.pressure,
        visibility = visibility,
        sunrise = sys.sunrise,
        sunset = sys.sunset,
        lat = coord.lat,
        lon = coord.lon
    )
}

fun ForecastDto.toForecastList(): List<ForecastItem> {
    return list.map { item ->
        ForecastItem(
            dateTime = item.dtTxt,
            temp = item.main.temp,
            description = item.weather[0].description,
            icon = item.weather[0].icon,
            minTemp = item.main.tempMin,
            maxTemp = item.main.tempMax,
            humidity = item.main.humidity,
            windSpeed = item.wind.speed
        )
    }
}

fun ForecastDto.toDailyForecast(): List<DayForecast> {
    return list.groupBy { 
        it.dtTxt.split(" ")[0] 
    }.map { (date, items) ->
        val maxTemp = items.maxOf { it.main.tempMax }
        val minTemp = items.minOf { it.main.tempMin }
        val mostFrequentIcon = items.groupBy { it.weather[0].icon }
            .maxByOrNull { it.value.size }?.key ?: items[0].weather[0].icon
        val description = items.groupBy { it.weather[0].description }
            .maxByOrNull { it.value.size }?.key ?: items[0].weather[0].description
            
        val dt = Instant.ofEpochSecond(items[0].dt).atZone(ZoneId.systemDefault()).toLocalDate()
        val dayName = dt.format(DateTimeFormatter.ofPattern("EEE", Locale.getDefault()))
        
        DayForecast(
            dayName = dayName,
            date = date,
            icon = mostFrequentIcon,
            minTemp = minTemp,
            maxTemp = maxTemp,
            description = description
        )
    }
}
