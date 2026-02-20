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
import com.example.weatherapp.utils.UnitConverter

@Composable
fun EventPlannerCard(
    weather: Weather,
    units: String = "metric",
    modifier: Modifier = Modifier
) {
    val tempInCelsius = UnitConverter.toCelsius(weather.temperature, units)
    val condition = weather.condition.lowercase()
    val humidity = weather.humidity
    val windInKmH = if (units == "metric") weather.windSpeed else weather.windSpeed * 1.609

    val activities = listOf(
        ActivityRating("🏏 Cricket/Sports", calculateSportsRating(tempInCelsius, condition, windInKmH)),
        ActivityRating("🍽️ Outdoor Dining", calculateDiningRating(tempInCelsius, condition)),
        ActivityRating("🧺 Picnic/Park", calculatePicnicRating(tempInCelsius, condition, humidity)),
        ActivityRating("🧺 Laundry Day", calculateLaundryRating(condition, humidity))
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), // Light Green
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📅", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Event & Activity Planner",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF2E7D32)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            activities.forEach { activity ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = activity.name, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { activity.rating / 100f },
                            modifier = Modifier.width(80.dp).height(6.dp),
                            color = getRatingColor(activity.rating),
                            trackColor = Color.LightGray.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${activity.rating}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = getRatingColor(activity.rating)
                        )
                    }
                }
            }
        }
    }
}

data class ActivityRating(val name: String, val rating: Int)

fun getRatingColor(rating: Int): Color {
    return when {
        rating > 80 -> Color(0xFF4CAF50)
        rating > 50 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }
}

// Logic functions
fun calculateSportsRating(temp: Double, condition: String, wind: Double): Int {
    var score = 100
    if (temp > 35 || temp < 10) score -= 30
    if (condition.contains("rain") || condition.contains("storm")) score -= 60
    if (wind > 25) score -= 20
    return score.coerceIn(0, 100)
}

fun calculateDiningRating(temp: Double, condition: String): Int {
    var score = 100
    if (temp > 30 || temp < 18) score -= 25
    if (condition.contains("rain") || condition.contains("clouds")) score -= 30
    return score.coerceIn(0, 100)
}

fun calculatePicnicRating(temp: Double, condition: String, humidity: Int): Int {
    var score = 100
    if (temp > 32 || temp < 20) score -= 20
    if (condition.contains("rain")) score -= 70
    if (humidity > 85) score -= 20
    return score.coerceIn(0, 100)
}

fun calculateLaundryRating(condition: String, humidity: Int): Int {
    var score = 100
    if (condition.contains("rain") || condition.contains("clouds")) score -= 50
    if (humidity > 70) score -= 30
    return score.coerceIn(0, 100)
}
