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
fun HealthWellnessCard(
    weather: Weather,
    aqi: Int,
    units: String = "metric",
    modifier: Modifier = Modifier
) {
    val tempInCelsius = UnitConverter.toCelsius(weather.temperature, units)
    val aqiText = when (aqi) {
        1 -> "Good"
        2 -> "Fair"
        3 -> "Moderate"
        4 -> "Poor"
        5 -> "Very Poor"
        else -> "Unknown"
    }
    
    val aqiColor = when (aqi) {
        1 -> Color(0xFF4CAF50)
        2 -> Color(0xFF8BC34A)
        3 -> Color(0xFFFFC107)
        4 -> Color(0xFFFF9800)
        5 -> Color(0xFFF44336)
        else -> Color.Gray
    }

    val healthAdvice = mutableListOf<String>()
    
    // AQI Advice
    if (aqi >= 3) healthAdvice.add("Air quality is moderate. Sensitive groups should limit outdoor exertion.")
    if (aqi >= 4) healthAdvice.add("Warning: High pollution levels. Wear a mask if heading out.")
    
    // Bio-Weather / Joint Pain / Migraine
    if (weather.humidity > 80 && tempInCelsius < 15) {
        healthAdvice.add("High humidity & cold: Increased risk of joint stiffness and pain. Keep yourself warm.")
    }
    if (tempInCelsius > 35) {
        healthAdvice.add("Extreme heat: Stay indoors during peak hours to avoid heatstroke and migraines.")
    }
    if (weather.pressure < 1000) {
        healthAdvice.add("Low barometric pressure: Some people may experience sinus pressure or headaches.")
    }

    if (healthAdvice.isEmpty()) {
        healthAdvice.add("Weather conditions are stable. Great day for outdoor activities and deep breathing!")
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🩺", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Health & Sehat",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                }
                
                Surface(
                    color = aqiColor.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "AQI: $aqiText",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = aqiColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            healthAdvice.forEach { advice ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text("•", color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = advice,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}
