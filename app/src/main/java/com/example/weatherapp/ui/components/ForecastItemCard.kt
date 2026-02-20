package com.example.weatherapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weatherapp.domain.model.ForecastItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

/**
 * ForecastItemCard
 * purpose: Displays a single forecast entry in the list.
 * It formats the date text from the API into a more readable time.
 */
@Composable
fun ForecastItemCard(
    item: ForecastItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(100.dp)
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val displayTime = formatDateTime(item.dateTime)
            Text(text = displayTime, style = MaterialTheme.typography.labelMedium)
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${item.icon}@2x.png",
                contentDescription = item.description,
                modifier = Modifier.size(50.dp)
            )
            Text(
                text = "${item.temp.roundToInt()}°",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

fun formatDateTime(dateTimeStr: String): String {
    return try {
        val parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        LocalDateTime.parse(dateTimeStr, parser).format(formatter)
    } catch (e: Exception) {
        dateTimeStr
    }
}

@Preview(showBackground = true)
@Composable
fun ForecastItemCardPreview() {
    MaterialTheme {
        ForecastItemCard(
            item = ForecastItem(
                dateTime = "2024-07-01 12:00:00",
                temp = 25.0,
                description = "Sunny",
                icon = "01d",
                minTemp = 20.0,
                maxTemp = 30.0,
                humidity = 50,
                windSpeed = 10.0
            )
        )
    }
}
