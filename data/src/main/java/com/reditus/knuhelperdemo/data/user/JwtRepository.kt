package com.reditus.knuhelperdemo.data.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class JwtToken(
    val accessToken: String,
    val refreshToken: String,
)

@Singleton
class JwtRepository @Inject constructor(
    private val dataStore : DataStore<Preferences>
){
    fun getJwtFlow(): Flow<JwtToken?>{
        return dataStore.data
            .map { prefs ->
                val accessToken = prefs[ACCESS_TOKEN_KEY]
                val refreshToken = prefs[REFRESH_TOKEN_KEY]
                if (accessToken != null && refreshToken != null) {
                    JwtToken(accessToken, refreshToken)
                } else null
            }
    }

    suspend fun save(jwtToken: JwtToken){
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = jwtToken.accessToken
            prefs[REFRESH_TOKEN_KEY] = jwtToken.refreshToken
        }
    }

    suspend fun delete(){
        dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN_KEY)
            prefs.remove(REFRESH_TOKEN_KEY)
        }
    }

    suspend fun saveAccessToken(accessToken: String) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = accessToken
        }
    }

    companion object{
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }
}