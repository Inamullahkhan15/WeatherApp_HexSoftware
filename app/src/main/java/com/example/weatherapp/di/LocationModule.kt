package com.example.weatherapp.di

import android.app.Application
import com.example.weatherapp.data.location.DefaultLocationTracker
import com.example.weatherapp.utils.LocationTracker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * LocationModule
 * purpose: Provides dependencies for location services.
 */
@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(app: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(app)
    }

    @Provides
    @Singleton
    fun provideLocationTracker(
        locationClient: FusedLocationProviderClient,
        app: Application
    ): LocationTracker {
        return DefaultLocationTracker(locationClient, app)
    }
}
