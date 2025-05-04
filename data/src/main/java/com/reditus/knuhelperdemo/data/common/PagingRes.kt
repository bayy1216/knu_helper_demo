package com.reditus.knuhelperdemo.data.common

data class PagingRes<T>(
    val hasNext: Boolean,
    val data: List<T>,
){
}