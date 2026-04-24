package com.example.focusboard.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private object Keys {
        val TOKEN = stringPreferencesKey("auth_token")
    }

    val token: Flow<String?> = dataStore.data.map { it[Keys.TOKEN] }

    suspend fun setToken(token: String) {
        dataStore.edit { prefs -> prefs[Keys.TOKEN] = token }
    }

    suspend fun clear() {
        dataStore.edit { prefs -> prefs.remove(Keys.TOKEN) }
    }
}

