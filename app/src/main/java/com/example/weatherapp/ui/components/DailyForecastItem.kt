package com.example.weatherapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.domain.model.DayForecast
import kotlin.math.roundToInt

@Composable
fun DailyForecastItem(
    item: DayForecast,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = item.dayName,
            modifier = Modifier.width(60.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
        
        AsyncImage(
            model = "https://openweathermap.org/img/wn/${item.icon}@2x.png",
            contentDescription = item.description,
            modifier = Modifier.size(40.dp)
        )

        Row(
            modifier = Modifier.width(100.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "${item.maxTemp.roundToInt()}°",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${item.minTemp.roundToInt()}°",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
    }
}
