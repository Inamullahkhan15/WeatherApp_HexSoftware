package com.example.weatherapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.domain.model.Weather
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun VitaminDTrackerCard(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    val condition = weather.condition.lowercase()
    val isDay = isDayTime(weather.sunrise, weather.sunset)
    
    val (advice, exposureMinutes) = when {
        !isDay -> "The sun has set. 🌙 Save your outdoor time for tomorrow morning!" to 0
        condition.contains("clear") -> "Perfect sunshine! ☀️ 10-15 minutes is enough for your daily Vitamin D." to 15
        condition.contains("clouds") -> "Slightly cloudy. ☁️ You might need 20-30 minutes for sufficient exposure." to 25
        condition.contains("rain") || condition.contains("storm") -> "Rainy day. 🌧️ Very little UV reaching. Consider a supplement if it stays like this!" to 0
        else -> "Moderate dhoop. ⛅ 20 minutes outside would be great." to 20
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)), // Light Orange
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("☀️", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sunlight & Vitamin D",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFE65100)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = advice,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            if (exposureMinutes > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        progress = { 0.7f }, // Aesthetic filler
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 3.dp,
                        color = Color(0xFFFB8C00)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Suggested Exposure: $exposureMinutes mins",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFEF6C00)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TimeInfo(label = "Sunrise", time = formatTime(weather.sunrise))
                TimeInfo(label = "Sunset", time = formatTime(weather.sunset))
            }
        }
    }
}

@Composable
fun TimeInfo(label: String, time: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(text = time, style = MaterialTheme.typography.bodySmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}

private fun isDayTime(sunrise: Long, sunset: Long): Boolean {
    val now = Instant.now().epochSecond
    return now in sunrise..sunset
}

private fun formatTime(timestamp: Long): String {
    return try {
        val dt = Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalTime()
        dt.format(DateTimeFormatter.ofPattern("h:mm a"))
    } catch (e: Exception) {
        "--:--"
    }
}
