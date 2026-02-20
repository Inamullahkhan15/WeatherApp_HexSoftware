package com.example.weatherapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * WeatherApp Class
 * purpose: This is the entry point of our application. 
 * We use @HiltAndroidApp to trigger Hilt's code generation, 
 * which includes a base class for our application that serves as the 
 * application-level dependency container.
 */
@HiltAndroidApp
class WeatherApp : Application()
