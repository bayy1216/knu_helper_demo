package com.reditus.knuhelperdemo.data.common

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class ServerInfo(
    val serverHost: String,
    val serverPort: Int,
    val isHttps: Boolean,
)

@Singleton
class ServerDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    fun getServerInfo(): Flow<ServerInfo> {
        return dataStore.data.map { prefs ->
            val serverUrl = prefs[SERVER_URL_KEY]
            val serverPort = prefs[SERVER_PORT_KEY]
            val isHttps = prefs[IS_HTTPS_KEY]
            if (serverUrl != null && serverPort != null && isHttps != null) {
                ServerInfo(serverUrl, serverPort, isHttps)
            } else {
                ServerInfo(
                    serverHost = "104.198.35.15",
                    serverPort = 8080,
                    isHttps = false,
                )
            }
        }
    }

    suspend fun saveServerInfo(serverInfo: ServerInfo) {
        dataStore.edit { prefs ->
            prefs[SERVER_URL_KEY] = serverInfo.serverHost
            prefs[SERVER_PORT_KEY] = serverInfo.serverPort
            prefs[IS_HTTPS_KEY] = serverInfo.isHttps
        }
    }

    companion object{
        private val SERVER_URL_KEY = stringPreferencesKey("server_url")
        private val SERVER_PORT_KEY = intPreferencesKey("server_port")
        private val IS_HTTPS_KEY = booleanPreferencesKey("is_https")
    }
}