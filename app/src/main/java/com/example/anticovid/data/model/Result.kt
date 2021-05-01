package com.example.anticovid.data.model

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result {

    data class Success<out T : Any>(val data: T) : Result()
    data class Error(val errorMessage: String) : Result()
}