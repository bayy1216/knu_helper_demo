package com.reditus.knuhelperdemo.data.notice

import kotlinx.serialization.Serializable

@Serializable
data class SubscribesRes(
    val data: List<SubscribeModel>,
)

@Serializable
data class SubscribeModel(
    val site: String,
    val color: String,
    val isAlarm: Boolean,
)
