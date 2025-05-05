package com.reditus.knuhelperdemo.data.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.reditus.knuhelperdemo.data.common.ktor.setupAuth
import com.reditus.knuhelperdemo.data.common.ktor.setupJson
import com.reditus.knuhelperdemo.data.common.ktor.setupLogging
import com.reditus.knuhelperdemo.data.common.ktor.setupServer
import com.reditus.knuhelperdemo.data.user.JwtRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    private val sendWithoutJwtUrls = listOf("/auth/login/v1", "/auth/signup/v1", "/auth/token")
    private const val KNU_DATASTORE = "knu_prefs.preferences_pb"

    @Singleton
    @Provides
    fun provideClient(
        jwtRepository: JwtRepository,
        serverDataSource: ServerDataSource,
    ): HttpClient {
        val serverInfo = runBlocking { serverDataSource.getServerInfo().first() }
        val client = HttpClient(CIO) {
            setupLogging()
            setupJson()
            setupServer(serverInfo)
            setupAuth(
                jwtRepository = jwtRepository,
                sendWithoutJwtUrls = sendWithoutJwtUrls,
            )
        }
        return client
    }



    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.applicationContext.filesDir.resolve(KNU_DATASTORE) }
        )
    }
}
