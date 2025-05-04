package com.reditus.knuhelperdemo.data.user

import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenRes(
    val accessToken: String,
)
@Serializable
data class JwtRes(
    val accessToken: String,
    val refreshToken: String,
){
    fun toToken(): JwtToken {
        return JwtToken(
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }
}