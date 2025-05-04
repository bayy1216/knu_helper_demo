package com.reditus.knuhelperdemo.data.notice

import kotlinx.serialization.Serializable

@Serializable
data class AddSubscribeReq(
    val site: String,
    val color: String,
    val alarm: Boolean,
)
@Serializable
data class DeleteSubscribeReq(
    val site: String,
)
