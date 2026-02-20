package com.example.weatherapp.ui.components

import androidx.compose.foundation.background
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
fun LifeHackCard(
    weather: Weather,
    units: String = "metric",
    modifier: Modifier = Modifier
) {
    val tempInCelsius = UnitConverter.toCelsius(weather.temperature, units)
    val windInKmH = if (units == "metric") weather.windSpeed else weather.windSpeed * 1.609

    val advice = when {
        tempInCelsius > 30 -> "It's scorching! 🥵 Stay hydrated and try to finish outdoor work before 11 AM."
        tempInCelsius < 5 -> "Brrr! ❄️ Perfect day for a hot chocolate. Dress in layers if you're heading out."
        weather.condition.lowercase().contains("rain") -> "Don't forget your umbrella! ☔ Also, driving might be slower than usual."
        weather.humidity > 80 -> "High humidity today. 💦 Great for your skin, but maybe not for your hair!"
        windInKmH > 32 -> "It's windy! 🌬️ Better secure loose items on your balcony/garden."
        else -> "Great weather for a walk! 👟 The air quality is looking good for outdoor exercise."
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0FE)),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💡", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Daily Insight",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1967D2)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = advice,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
        }
    }
}
