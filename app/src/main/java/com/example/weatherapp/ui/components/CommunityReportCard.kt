package com.example.weatherapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.data.local.WeatherReport

@Composable
fun CommunityReportCard(
    cityName: String,
    reports: List<WeatherReport>,
    onReportSubmit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Raining 🌧️", "Sunny ☀️", "Very Windy 🌬️", "Foggy 🌫️", "Clear Sky ✨")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE)), // Light Blue
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🤳", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Community Reports",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF0277BD)
                    )
                    Text(
                        text = "What's the weather like at your home?",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Options to report
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.take(3).forEach { option ->
                    SuggestionChip(
                        onClick = { onReportSubmit(option) },
                        label = { Text(option.split(" ")[0], fontSize = 11.sp) }
                    )
                }
            }

            if (reports.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Recent reports in $cityName:",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.DarkGray
                )
                
                reports.forEach { report ->
                    Text(
                        text = "• Someone reported ${report.status}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF01579B),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
