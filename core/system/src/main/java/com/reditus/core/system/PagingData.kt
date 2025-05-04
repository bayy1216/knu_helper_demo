package com.reditus.core.system

data class PagingData<T>(
    val data: List<T>,
    val state: PagingState,
    val page: Int,
) {
    val hasNext: Boolean
        get() = state != PagingState.NOT_HAS_NEXT

    fun isFetchingMore(): Boolean {
        return state.isFetchingMore()
    }

    fun copyFrom(
        data: List<T>,
        hasNext: Boolean,
        page: Int,
    ): PagingData<T> {
        return this.copy(
            data = data,
            state = if (hasNext) PagingState.HAS_NEXT else PagingState.NOT_HAS_NEXT,
            page = page
        )
    }
}

enum class PagingState{
    FORCE_REFRESH,
    LOADING_MORE,
    HAS_NEXT,
    NOT_HAS_NEXT;
    fun isFetchingMore(): Boolean{
        return this == LOADING_MORE || this == FORCE_REFRESH
    }
}