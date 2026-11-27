package com.notely.app.data.repository

import com.notely.app.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val store: SettingsDataStore) {

    /** "system", "clair" ou "sombre". */
    val theme: Flow<String> = store.theme

    val notificationsEnabled: Flow<Boolean> = store.notificationsEnabled

    /** "fr" pour l'instant. */
    val language: Flow<String> = store.language

    suspend fun setTheme(theme: String) = store.setTheme(theme)

    suspend fun setNotificationsEnabled(enabled: Boolean) =
        store.setNotificationsEnabled(enabled)

    suspend fun setLanguage(language: String) = store.setLanguage(language)
}
