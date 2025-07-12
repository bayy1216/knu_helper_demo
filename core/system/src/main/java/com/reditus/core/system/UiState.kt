package com.reditus.core.system

import arrow.core.Either

sealed class UiState<out T, out ERROR>{
    data object Loading : UiState<Nothing, Nothing>()
    data class Success<out T>(val data: T) : UiState<T, Nothing>()
    data class Error<out ERROR>(val error:ERROR) : UiState<Nothing, ERROR>()
}

fun <L, R> Either<L, R>.toUiState(): UiState<R, L> = fold(
    ifLeft = { UiState.Error(it) },
    ifRight = { UiState.Success(it) }
)