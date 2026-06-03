package com.example.weatherapp.utils

/**
 * Resource.kt
 *
 * Sealed class for handling asynchronous data states in a type-safe manner.
 * Represents three possible states of a data operation: Loading, Success, or Error.
 *
 * This pattern is commonly used in modern Android apps (Google's architecture samples)
 * to manage UI state based on data fetch status.
 *
 * Usage Example:
 * ```
 * when (resource) {
 *     is Resource.Loading -> showProgressBar()
 *     is Resource.Success -> displayData(resource.data)
 *     is Resource.Error -> showErrorMessage(resource.message)
 * }
 * ```
 *
 * @param T The type of data being managed
 * @param data The actual data (null for Loading/Error states)
 * @param message Optional error message for failed operations
 *
 * @author Weather App Team
 * @version 1.0
 */
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    /**
     * Represents successful data operation.
     * @param data The retrieved data of type T
     */
    class Success<T>(data: T) : Resource<T>(data)
    
    /**
     * Represents failed data operation.
     * @param message Human-readable error description
     * @param data Optional partial data (useful for retry scenarios)
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    
    /**
     * Represents ongoing data operation.
     * UI typically shows loading indicator in this state.
     * @param data Optional partial data while loading
     */
    class Loading<T>(data: T? = null) : Resource<T>(data)
}
