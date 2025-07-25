package com.reditus.knuhelperdemo.data.common.ktor

import android.util.Log
import com.reditus.knuhelperdemo.data.common.ServerInfo
import com.reditus.knuhelperdemo.data.user.AccessTokenRes
import com.reditus.knuhelperdemo.data.user.JwtRepository
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json


fun HttpClientConfig<CIOEngineConfig>.setupLogging() {
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Log.d("KtorClient", message)
            }
        }
        level = LogLevel.ALL
        // sanitizeHeader { header -> header == HttpHeaders.Authorization }
    }
}

fun HttpClientConfig<CIOEngineConfig>.setupJson() {
    install(ContentNegotiation) {
        json(
            Json {
                isLenient = true
                prettyPrint = true
            }
        )
    }
}

fun HttpClientConfig<CIOEngineConfig>.setupServer(
    serverInfo: ServerInfo,
) {
    install(DefaultRequest) {
        header(HttpHeaders.ContentType, "application/json")
        header(HttpHeaders.Accept, "application/json")
        url {
            protocol = if (serverInfo.isHttps) URLProtocol.HTTPS else URLProtocol.HTTP
            host = serverInfo.serverHost
            port = serverInfo.serverPort
        }
    }
}


fun HttpClientConfig<CIOEngineConfig>.setupAuth(
    jwtRepository: JwtRepository,
    sendWithoutJwtUrls: List<String>,
) {
    install(Auth) {
        bearer {
            loadTokens {
                val jwt = jwtRepository.getJwtFlow().first()
                jwt?.let {
                    BearerTokens(
                        accessToken = it.accessToken,
                        refreshToken = it.refreshToken
                    )
                }
            }
            refreshTokens {
                if (oldTokens != null) {
                    val jwt = oldTokens!!
                    return@refreshTokens try {
                        val res = client.post("/auth/token") {
                            headers[HttpHeaders.Authorization] = "Bearer ${jwt.refreshToken}"
                        }.body<AccessTokenRes>()
                        jwtRepository.saveAccessToken(res.accessToken)

                        BearerTokens(
                            accessToken = res.accessToken,
                            refreshToken = jwt.refreshToken
                        )
                    } catch (_: Exception) {
                        jwtRepository.delete()
                        client.authProvider<BearerAuthProvider>()?.clearToken()
                        null
                    }
                } else {
                    // Emit Refresh Event
                    null
                }
            }
            // sendWithoutRequest가 true면 사전인증에 해당
            // 사전인증: 요청 보내기 전에 헤더 붙임 (sendWithoutRequest == true)
            // 도전 기반 인증 (challenge-based auth): 401 응답 받고 나서 붙임 (sendWithoutRequest == false)
            sendWithoutRequest { request ->
                // If it is a login request, do not send the token
                val shouldNotJwt = sendWithoutJwtUrls.any {
                    request.url.encodedPath == it
                }
                Log.d("Request", "Not Apply JWT Request: ${shouldNotJwt}")
                !shouldNotJwt
            }
        }
    }
}

fun HttpClientConfig<CIOEngineConfig>.setupErrorHandling(){
    expectSuccess = true
    HttpResponseValidator {
        handleResponseExceptionWithRequest { exception, request ->
            val clientException = exception as? ClientRequestException ?: return@handleResponseExceptionWithRequest
            throw KnuhelperServerError.from(clientException)
        }
    }
}