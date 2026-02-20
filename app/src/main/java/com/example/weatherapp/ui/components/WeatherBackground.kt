package com.example.weatherapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

/**
 * WeatherBackground
 * purpose: Provides a dynamic gradient background based on the weather condition.
 */
@Composable
fun WeatherBackground(
    condition: String,
    content: @Composable () -> Unit
) {
    val gradientColors = when (condition.lowercase()) {
        "clear" -> listOf(Color(0xFF4A90E2), Color(0xFFF5A623)) // Sunny
        "clouds" -> listOf(Color(0xFF757575), Color(0xFFBDBDBD)) // Cloudy
        "rain", "drizzle" -> listOf(Color(0xFF4A4A4A), Color(0xFF4A90E2)) // Rain
        "thunderstorm" -> listOf(Color(0xFF212121), Color(0xFF757575)) // Storm
        else -> listOf(Color(0xFF4A90E2), Color(0xFFE3F2FD)) // Default blue
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        content()
    }
}

@Preview
@Composable
fun SunnyBackgroundPreview() {
    WeatherBackground(condition = "clear") {
        Box(modifier = Modifier.fillMaxSize())
    }
}

@Preview
@Composable
fun RainyBackgroundPreview() {
    WeatherBackground(condition = "rain") {
        Box(modifier = Modifier.fillMaxSize())
    }
}
