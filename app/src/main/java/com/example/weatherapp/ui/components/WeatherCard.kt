package com.example.weatherapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.domain.model.Weather
import kotlin.math.roundToInt

/**
 * WeatherCard
 * purpose: Displays the main weather details like City, Temperature, and Condition.
 * We use Coil's AsyncImage to load the weather icon from OpenWeatherMap.
 */
@Composable
fun WeatherCard(
    weather: Weather,
    modifier: Modifier = Modifier,
    contentColor: Color = Color.Black,
    units: String = "metric",
    currentTime: String = "",
    onUnitToggle: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/${weather.icon}@4x.png",
                    contentDescription = weather.description,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "${weather.temperature.roundToInt()}",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 80.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Normal
                            ),
                            color = contentColor
                        )
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            Text(
                                text = if (units == "metric") "°C | °F" else "°F | °C",
                                style = MaterialTheme.typography.titleMedium,
                                color = contentColor.copy(alpha = 0.6f),
                                modifier = Modifier.clickable { onUnitToggle() }
                            )
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = weather.cityName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = contentColor
                )
                Text(
                    text = if (currentTime.isNotEmpty()) "Today, $currentTime" else formatTimestamp(weather.timestamp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.6f)
                )
                Text(
                    text = weather.description.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            WeatherMinimalDetail(label = "Precipitation", value = "0%", color = contentColor.copy(alpha = 0.6f)) 
            Spacer(modifier = Modifier.width(16.dp))
            WeatherMinimalDetail(label = "Humidity", value = "${weather.humidity}%", color = contentColor.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.width(16.dp))
            val windUnit = if (units == "metric") "km/h" else "mph"
            WeatherMinimalDetail(label = "Wind", value = "${weather.windSpeed.roundToInt()} $windUnit", color = contentColor.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun WeatherMinimalDetail(label: String, value: String, color: Color = Color.Gray) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodySmall,
        color = color
    )
}

private fun formatTimestamp(timestamp: Long): String {
    return try {
        val sdf = java.text.SimpleDateFormat("EEEE h:mm a", java.util.Locale.getDefault())
        val netDate = java.util.Date(timestamp * 1000L)
        sdf.format(netDate)
    } catch (e: Exception) {
        ""
    }
}
