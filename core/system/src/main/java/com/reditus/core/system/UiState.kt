package com.reditus.core.system

sealed class UiState<out T, out E>{
    data object Loading : UiState<Nothing, Nothing>()
    data class Success<out T>(val data: T) : UiState<T, Nothing>()
    data class Error<out E>(val error:E) : UiState<Nothing, E>()
}