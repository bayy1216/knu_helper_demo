package com.reditus.core.system

data class PagingData<T, ERROR>(
    val data: List<T>,
    val page: Int,
    val hasNext: Boolean,
    val state: PagingState<ERROR>,
) {
}

sealed class PagingState<out E> {
    data object Success : PagingState<Nothing>()
    data object LoadingMore : PagingState<Nothing>()
    data object ForceRefreshing : PagingState<Nothing>()
    data class Error<E>(val error: E) : PagingState<E>()
}