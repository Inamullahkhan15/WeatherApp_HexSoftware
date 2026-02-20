package com.example.weatherapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.domain.model.ForecastItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun HourlyGraph(
    hourlyForecast: List<ForecastItem>,
    selectedTab: Int = 0,
    modifier: Modifier = Modifier
) {
    if (hourlyForecast.isEmpty()) return

    val scrollState = rememberScrollState()
    val itemWidth = 70.dp
    
    // Choose data based on tab
    val data = hourlyForecast.map { item ->
        when (selectedTab) {
            0 -> item.temp
            1 -> item.humidity.toDouble()
            2 -> item.windSpeed
            else -> item.temp
        }
    }
    
    val graphColor = when (selectedTab) {
        0 -> Color(0xFFFFC107) // Amber for Temp
        1 -> Color(0xFF2196F3) // Blue for Humidity
        2 -> Color(0xFF4CAF50) // Green for Wind
        else -> Color(0xFFFFC107)
    }

    val maxVal = data.maxOf { it }
    val minVal = data.minOf { it }
    val range = (maxVal - minVal).coerceAtLeast(1.0).toFloat()

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .horizontalScroll(scrollState)
        ) {
            Canvas(
                modifier = Modifier
                    .width(itemWidth * hourlyForecast.size)
                    .fillMaxHeight()
                    .padding(vertical = 40.dp)
            ) {
                val widthPx = size.width
                val heightPx = size.height
                val spacing = itemWidth.toPx()

                val points = data.mapIndexed { index, value ->
                    val x = index * spacing + spacing / 2
                    val ratio = (value - minVal) / range
                    val y = heightPx - (ratio.toFloat() * heightPx)
                    Offset(x, y)
                }

                // Draw Smooth Line
                val path = Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            val prev = points[i - 1]
                            val curr = points[i]
                            val cp1 = Offset(prev.x + (curr.x - prev.x) / 2, prev.y)
                            val cp2 = Offset(prev.x + (curr.x - prev.x) / 2, curr.y)
                            cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, curr.x, curr.y)
                        }
                    }
                }

                drawPath(
                    path = path,
                    color = graphColor,
                    style = Stroke(width = 3.dp.toPx())
                )

                // Fill area under the curve
                val fillPath = Path().apply {
                    addPath(path)
                    if (points.isNotEmpty()) {
                        lineTo(points.last().x, heightPx)
                        lineTo(points.first().x, heightPx)
                        close()
                    }
                }
                drawPath(
                    path = fillPath,
                    color = graphColor.copy(alpha = 0.1f)
                )
            }

            // Labels for Value and Time
            Row(
                modifier = Modifier.width(itemWidth * hourlyForecast.size)
            ) {
                hourlyForecast.forEachIndexed { index, item ->
                    val value = data[index]
                    val displayValue = when (selectedTab) {
                        0 -> "${value.roundToInt()}°"
                        1 -> "${value.roundToInt()}%"
                        2 -> "${value.roundToInt()}"
                        else -> "${value.roundToInt()}"
                    }

                    Box(
                        modifier = Modifier
                            .width(itemWidth)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        val ratio = (value - minVal) / range
                        val topPadding = (1.0 - ratio) * 120 // Rough estimate to match graph
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = (40 + topPadding).dp)
                        ) {
                            Text(
                                text = displayValue,
                                fontSize = 12.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = graphColor
                            )
                        }
                        
                        Text(
                            text = formatToTime(item.dateTime),
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

private fun formatToTime(dateTimeStr: String): String {
    return try {
        val parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formatter = DateTimeFormatter.ofPattern("h a")
        LocalDateTime.parse(dateTimeStr, parser).format(formatter).lowercase()
    } catch (e: Exception) {
        dateTimeStr
    }
}
