package com.example.weatherapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * WeatherDao
 * purpose: Interface for Room to interact with the favorite_cities table.
 */
@Dao
interface WeatherDao {

    @Query("SELECT * FROM favorite_cities ORDER BY addedTimestamp DESC")
    fun getFavoriteCities(): Flow<List<FavoriteCity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteCity(city: FavoriteCity)

    @Delete
    suspend fun removeFavoriteCity(city: FavoriteCity)

    @Query("SELECT EXISTS(SELECT * FROM favorite_cities WHERE cityName = :cityName)")
    suspend fun isFavorite(cityName: String): Boolean

    // Community Reports
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: WeatherReport)

    @Query("SELECT * FROM weather_reports WHERE cityName = :cityName ORDER BY timestamp DESC LIMIT 5")
    fun getReportsForCity(cityName: String): Flow<List<WeatherReport>>
}
