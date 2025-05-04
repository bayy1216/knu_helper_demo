package com.reditus.knuhelperdemo.data.user

import kotlinx.serialization.Serializable

@Serializable
data class SignupReq(
    val uuid: String,
    val fcmToken: String,
)