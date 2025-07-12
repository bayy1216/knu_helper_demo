package com.reditus.knuhelperdemo.data.user

import arrow.core.Either
import com.reditus.knuhelperdemo.data.common.ServerError
import com.reditus.knuhelperdemo.data.common.unwrapToServerError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val client: HttpClient
){

    /// 회원가입
    suspend fun signup(
        signupReq: SignupReq,
    ): Either<ServerError,JwtRes> = Either.catch{
        client.post("/auth/signup/v1"){
            setBody(signupReq)
        }.body<JwtRes>()
    }.mapLeft {
        it.unwrapToServerError()
    }

    /// 로그인
    suspend fun login(uuid:String): Either<ServerError,JwtRes> = Either.catch{
        client.post("/auth/login/v1"){
            headers[HttpHeaders.Authorization] = "Basic $uuid"
        }.body<JwtRes>()
    }.mapLeft {
        it.unwrapToServerError()
    }
}