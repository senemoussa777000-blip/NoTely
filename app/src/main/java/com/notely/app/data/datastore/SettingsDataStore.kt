package com.notely.app.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "notely_settings"
)

/** Préférences utilisateur persistées avec DataStore (thème, notifications, langue). */
class SettingsDataStore(context: Context) {

    private val dataStore = context.settingsDataStore

    val theme: Flow<String> = dataStore.data.map { it[Keys.THEME] ?: "system" }

    val notificationsEnabled: Flow<Boolean> =
        dataStore.data.map { it[Keys.NOTIFICATIONS] ?: true }

    val language: Flow<String> = dataStore.data.map { it[Keys.LANGUAGE] ?: "fr" }

    suspend fun setTheme(theme: String) {
        dataStore.edit { it[Keys.THEME] = theme }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.NOTIFICATIONS] = enabled }
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { it[Keys.LANGUAGE] = language }
    }

    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
        val LANGUAGE = stringPreferencesKey("language")
    }
}
