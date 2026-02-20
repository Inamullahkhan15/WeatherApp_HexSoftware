package com.example.weatherapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * WeatherDatabase
 * purpose: Main database class for our application.
 */
@Database(entities = [FavoriteCity::class, WeatherReport::class], version = 2, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract val dao: WeatherDao
}
