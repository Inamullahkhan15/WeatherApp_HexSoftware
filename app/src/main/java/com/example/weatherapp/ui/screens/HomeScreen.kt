package com.example.weatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherapp.ui.components.*
import com.example.weatherapp.viewmodel.WeatherState
import com.example.weatherapp.viewmodel.WeatherViewModel

@Composable
fun HomeScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val reports by viewModel.cityReports.collectAsState(initial = emptyList())
    
    val backgroundColor = remember(state.weather) {
        val weather = state.weather
        if (weather == null) Color.White
        else {
            val isNight = System.currentTimeMillis() / 1000 !in weather.sunrise..weather.sunset
            when {
                isNight -> Color(0xFF1A237E) // Deep Indigo for Night
                weather.condition.lowercase().contains("rain") -> Color(0xFF607D8B) // Blue Grey for Rain
                weather.condition.lowercase().contains("cloud") -> Color(0xFF90A4AE) // Light Blue Grey for Clouds
                else -> Color(0xFFE3F2FD) // Light Blue for Day/Clear
            }
        }
    }
    
    val contentColor = if (state.weather != null && (System.currentTimeMillis() / 1000 !in state.weather.sunrise..state.weather.sunset)) Color.White else Color.Black

    var currentTime by remember { mutableStateOf(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))) }
    
    LaunchedEffect(Unit) {
        while(true) {
            currentTime = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))
            kotlinx.coroutines.delay(60000)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
    ) {
        item {
            SearchHeader(
                onSearch = { viewModel.getWeather(it) },
                onPreciseLocation = { viewModel.loadWeatherInfo() }
            )
        }

        if (state.isLoading && state.weather == null) {
            item {
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = if (backgroundColor == Color.White) Color.Blue else Color.White)
                }
            }
        } else if (state.error.isNotEmpty()) {
            item {
                Text(
                    text = state.error,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            state.weather?.let { weather ->
                item {
                    WeatherCard(
                        weather = weather,
                        contentColor = contentColor,
                        units = state.units,
                        currentTime = currentTime,
                        onUnitToggle = { viewModel.toggleUnits() }
                    )
                }

                item {
                    SectionDivider("Personal Insights", color = contentColor.copy(alpha = 0.7f))
                    LifeHackCard(weather = weather, units = state.units)
                }

                item {
                    HealthWellnessCard(weather = weather, aqi = state.aqi, units = state.units)
                }

                item {
                    ClothingAdvisorCard(weather = weather, units = state.units)
                }

                item {
                    SectionDivider("Plan Your Day", color = contentColor.copy(alpha = 0.7f))
                    EventPlannerCard(weather = weather, units = state.units)
                }

                item {
                    VitaminDTrackerCard(weather = weather)
                }

                item {
                    SectionDivider("Community", color = contentColor.copy(alpha = 0.7f))
                    CommunityReportCard(
                        cityName = weather.cityName,
                        reports = reports,
                        onReportSubmit = { status -> viewModel.submitReport(weather.cityName, status) }
                    )
                }

                item {
                    SectionDivider("Forecast", color = contentColor.copy(alpha = 0.7f))
                    var selectedForecastTab by remember { mutableStateOf(0) }
                    
                    WeatherTabs(
                        selectedTabIndex = selectedForecastTab,
                        onTabSelected = { selectedForecastTab = it }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    HourlyGraph(
                        hourlyForecast = state.hourlyForecast,
                        selectedTab = selectedForecastTab
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                }

                items(state.dailyForecast) { day ->
                    DailyForecastItem(item = day)
                }
                
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun SectionDivider(title: String, color: Color = Color.Gray) {
    Text(
        text = title,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
        color = color
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHeader(
    onSearch: (String) -> Unit,
    onPreciseLocation: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search city...") },
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.extraLarge,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onSearch(query); query = "" }) {
                        Text("Go", color = MaterialTheme.colorScheme.primary, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }
            }
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        TextButton(onClick = onPreciseLocation) {
            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Precise", fontSize = 12.sp)
        }
    }
}

@Composable
fun WeatherTabs(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Temperature", "Precipitation", "Wind")

    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent, // Make it blend with background
        contentColor = Color(0xFFFFC107),
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = Color(0xFFFFC107)
            )
        },
        divider = {}
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        color = if (selectedTabIndex == index) Color.Black else Color.Gray.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            )
        }
    }
}
