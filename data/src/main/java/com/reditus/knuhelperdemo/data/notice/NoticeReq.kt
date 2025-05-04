package com.reditus.knuhelperdemo.data.notice

import kotlinx.serialization.Serializable

@Serializable
data class NoticePagingReq(
    val page: Int = 0,
    val size: Int = 20,
    val site: String? = null,
    val title: String? = null,
) {
}
