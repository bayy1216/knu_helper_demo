package com.reditus.core.system

data class PagingData<T>(
    val data: List<T>,
    val page: Int,
    val hasNext: Boolean,
    val state: PagingState,
) {
}

sealed class PagingState {
    data object Success : PagingState()
    data object LoadingMore : PagingState()
    data object ForceRefreshing : PagingState()
    data class Error(val throwable: Throwable) : PagingState()
}