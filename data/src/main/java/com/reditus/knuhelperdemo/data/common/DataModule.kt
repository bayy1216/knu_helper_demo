package com.reditus.knuhelperdemo.data.common

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.reditus.knuhelperdemo.data.user.JwtRepository
import com.reditus.knuhelperdemo.data.user.JwtRes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    private val refreshMutex = Mutex() // 리프래시 토큰 동기화를 위한 뮤텍스


    @Singleton
    @Provides
    fun provideClient(
        jwtRepository: JwtRepository
    ): HttpClient {
        val client = HttpClient(CIO) {
            install(Logging){
                logger = Logger.DEFAULT
                level = LogLevel.ALL
                sanitizeHeader { header -> header == HttpHeaders.Authorization }
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("KtorClient", message)
                    }
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    prettyPrint = true
                })
            }
            install(DefaultRequest) {
                header(HttpHeaders.ContentType, "application/json")
                header(HttpHeaders.Accept, "application/json")
                url {
                    protocol = io.ktor.http.URLProtocol.HTTP
                    host = "104.198.35.15"
                    port = 8080
                }
            }
        }
        client.plugin(HttpSend).intercept { request ->
            // Add custom headers or modify the request here
            val jwtToken = jwtRepository.getJwtFlow().first()
            val originRequest = HttpRequestBuilder().apply {
                takeFrom(request)
                if(jwtToken != null && !request.headers.contains(HEADER_NO_JWT)){
                    headers.remove(HttpHeaders.Authorization)
                    headers.append(HttpHeaders.Authorization, "Bearer ${jwtToken.accessToken}")
                }
            }
            val result = execute(originRequest)

            if(result.response.status.value == 401){
                refreshMutex.withLock {// 리프래시 토큰 동기화 락 진입
                    val currToken = jwtRepository.getJwtFlow().first()
                    if(currToken != null && currToken != jwtToken){// 다른 락에서 이미 재발급 받은 경우
                        val retryRequest = HttpRequestBuilder().apply {
                            takeFrom(originRequest)
                            headers.remove(HttpHeaders.Authorization)
                            headers.append(HttpHeaders.Authorization, "Bearer ${currToken.accessToken}")
                        }
                        return@intercept execute(retryRequest)
                    }
                    val refreshToken = currToken?.refreshToken
                    if(refreshToken != null){
                        try{
                            val tokenResult = client.post("/auth/refresh/v1") {
                                header(HttpHeaders.ContentType, "application/json")
                                header(HttpHeaders.Accept, "application/json")
                                header(HttpHeaders.Authorization, "Bearer $refreshToken")
                            }
                            val newJwt = tokenResult.body<JwtRes>().toToken()
                            jwtRepository.save(newJwt)

                            val retryRequest = HttpRequestBuilder().apply {
                                takeFrom(originRequest)
                                headers.remove(HttpHeaders.Authorization)
                                headers.append(HttpHeaders.Authorization, "Bearer ${newJwt.accessToken}")
                            }

                            return@intercept execute(retryRequest)
                        }catch (e:Exception){
                            //TODO : 리프래시 토큰 만료 이벤트 발행
                            jwtRepository.delete()
                            Log.e("KtorClient", "Error deleting JWT token", e)
                        }
                    }
                }

            }
            return@intercept result
        }
        return client
    }

    const val HEADER_NO_JWT = "NoJwt"
    private const val KNU_DATASTORE = "knu_prefs.preferences_pb"

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.applicationContext.filesDir.resolve(KNU_DATASTORE) }
        )
    }
}