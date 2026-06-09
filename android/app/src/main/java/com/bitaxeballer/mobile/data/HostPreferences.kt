package com.bitaxeballer.mobile.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class HostPreferences(private val context: Context) {
    private val hostKey = stringPreferencesKey("dashboard_host")

    val baseUrl: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[hostKey] ?: DEFAULT_BASE_URL
    }

    suspend fun setBaseUrl(url: String) {
        context.dataStore.edit { prefs -> prefs[hostKey] = url.trim().trimEnd('/') }
    }
}
