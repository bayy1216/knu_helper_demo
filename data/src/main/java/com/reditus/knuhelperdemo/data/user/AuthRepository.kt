package com.reditus.knuhelperdemo.data.user

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
    ): JwtRes {
        return client.post("/auth/signup/v1"){
            setBody(signupReq)
        }.body()
    }

    /// 로그인
    suspend fun login(uuid:String): JwtRes {
        return client.post("/auth/login/v1"){
            headers[HttpHeaders.Authorization] = "Basic $uuid"
        }.body()
    }
}