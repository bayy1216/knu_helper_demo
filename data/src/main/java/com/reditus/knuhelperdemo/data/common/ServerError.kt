package com.reditus.knuhelperdemo.data.common

import com.reditus.knuhelperdemo.data.common.ktor.KnuhelperServerError

sealed interface ServerError {
    data object Unknown : ServerError
    data class ErrorResponse(
        val message: String,
        val code: Int,
    ) : ServerError
}

fun KnuhelperServerError.toServerError():ServerError.ErrorResponse = ServerError.ErrorResponse(
    code = code,
    message = message
)


fun Throwable.unwrapToServerError(): ServerError {
    return when (this) {
        is KnuhelperServerError -> this.toServerError()
        else -> ServerError.Unknown
    }
}