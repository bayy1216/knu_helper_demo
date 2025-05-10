package com.reditus.knuhelperdemo.data.common

import kotlinx.serialization.Serializable

@Serializable
data class PagingRes<T>(
    val hasNext: Boolean,
    val data: List<T>,
){
}