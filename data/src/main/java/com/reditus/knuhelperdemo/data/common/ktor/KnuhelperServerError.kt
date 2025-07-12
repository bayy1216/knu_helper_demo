package com.reditus.knuhelperdemo.data.common.ktor

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException

data class ErrorResponse(
    val message: String,
)

class KnuhelperServerError(
    override val message:String,
    val code :Int,
) : Throwable(message) {

    companion object{
        suspend fun from(e: ClientRequestException):KnuhelperServerError{
            val res = e.response
            val body :ErrorResponse = res.body()
            return KnuhelperServerError(
                message = body.message,
                code = res.status.value,
            )
        }
    }
}