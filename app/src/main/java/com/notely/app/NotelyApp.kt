package com.notely.app

import android.app.Application
import com.notely.app.data.datastore.SettingsDataStore
import com.notely.app.data.local.NotelyDatabase
import com.notely.app.data.repository.EventRepository
import com.notely.app.data.repository.NoteRepository
import com.notely.app.data.repository.SettingsRepository
import com.notely.app.data.repository.TaskRepository

/**
 * Point d'entrée de l'application.
 * Crée une seule fois la base Room et les repositories, partagés par tous les écrans.
 */
class NotelyApp : Application() {

    val database: NotelyDatabase by lazy { NotelyDatabase.getInstance(this) }

    val noteRepository: NoteRepository by lazy {
        NoteRepository(database.noteDao())
    }

    val taskRepository: TaskRepository by lazy {
        TaskRepository(applicationContext, database.taskDao())
    }

    val eventRepository: EventRepository by lazy {
        EventRepository(applicationContext, database.eventDao())
    }

    val settingsDataStore: SettingsDataStore by lazy { SettingsDataStore(this) }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(settingsDataStore)
    }
}
