package com.example.weatherapp.utils

/**
 * Resource Sealed Class
 * purpose: Handles data states: Success, Error, and Loading.
 * This is very useful for UI to show a loader or an error message based on the status.
 */
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}
