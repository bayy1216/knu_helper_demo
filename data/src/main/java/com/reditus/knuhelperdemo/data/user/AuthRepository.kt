package com.reditus.knuhelperdemo.data.user

import com.reditus.knuhelperdemo.data.common.DataModule.HEADER_NO_JWT
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
            headers[HEADER_NO_JWT] = "Signup"
            setBody(signupReq)
        }.body()
    }

    /// 로그인
    suspend fun login(uuid:String): JwtRes {
        return client.post("/auth/login/v1"){
            headers[HEADER_NO_JWT] = "Login"
            headers[HttpHeaders.Authorization] = "Basic $uuid"
        }.body()
    }
}