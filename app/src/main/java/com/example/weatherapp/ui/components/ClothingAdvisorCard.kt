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
fun ClothingAdvisorCard(
    weather: Weather,
    units: String = "metric",
    modifier: Modifier = Modifier
) {
    val tempInCelsius = UnitConverter.toCelsius(weather.temperature, units)
    val condition = weather.condition.lowercase()
    val windInKmH = if (units == "metric") weather.windSpeed else weather.windSpeed * 1.609

    val advice = when {
        tempInCelsius > 35 -> "Extremely hot! 👕 Wear very light cotton clothes and a hat to protect from direct sun."
        tempInCelsius > 25 -> "Warm day. 👕 A simple T-shirt and sunglasses will be perfect."
        tempInCelsius in 15.0..25.0 -> "Pleasant weather. 🧥 A light shirt or a thin hoodie is recommended for the evening."
        tempInCelsius in 5.0..15.0 -> "Chilly. 🧥 Wear a proper jacket or a sweater. Don't forget socks!"
        tempInCelsius < 5 -> "Freezing! 🧥🧤 Heavy coat, gloves, and a muffler are essential today."
        else -> "Check temperature again."
    }

    val extraAdvice = mutableListOf<String>()
    if (condition.contains("rain") || condition.contains("drizzle")) {
        extraAdvice.add("Carry an umbrella or a raincoat! ☔ Your shoes might get wet.")
    }
    if (windInKmH > 32) { // approx 20 mph
        extraAdvice.add("Very windy! 🌬️ Avoid loose scarves or caps that might fly away.")
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)), // Light purple
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("👕", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Wardrobe Assistant",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF7B1FA2)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = advice,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            if (extraAdvice.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                extraAdvice.forEach { extra ->
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        Text("💡", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = extra,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4A148C)
                        )
                    }
                }
            }
        }
    }
}
