package com.notely.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notely.app.data.repository.EventRepository
import com.notely.app.data.repository.NoteRepository
import com.notely.app.data.repository.SettingsRepository
import com.notely.app.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class StorageInfo(
    val notes: Int = 0,
    val tasks: Int = 0,
    val events: Int = 0
)

class ProfileViewModel(
    private val settingsRepository: SettingsRepository,
    private val noteRepository: NoteRepository,
    private val taskRepository: TaskRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    val theme: Flow<String> = settingsRepository.theme

    val notificationsEnabled: Flow<Boolean> = settingsRepository.notificationsEnabled

    val language: Flow<String> = settingsRepository.language

    val storage: StateFlow<StorageInfo> = combine(
        noteRepository.countActiveNotes(),
        taskRepository.countAll(),
        eventRepository.getAllEvents()
    ) { notes, tasks, events ->
        StorageInfo(notes = notes, tasks = tasks, events = events.size)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StorageInfo())

    fun setTheme(theme: String) = viewModelScope.launch {
        settingsRepository.setTheme(theme)
    }

    fun setNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        settingsRepository.setNotificationsEnabled(enabled)
    }

    /** Exporte toutes les données en JSON (notes, tâches, événements). */
    suspend fun exportJson(): String {
        val notes = noteRepository.getActiveNotes().first()
        val archived = noteRepository.getArchivedNotes().first()
        val tasks = taskRepository.getAllTasks().first()
        val events = eventRepository.getAllEvents().first()
        val sb = StringBuilder()
        sb.append("{\n  \"app\": \"NOTELY\",\n  \"version\": \"1.0.0\",\n")
        sb.append("  \"notes\": [")
        (notes + archived).joinToString(",\n") { note ->
            "\n    {\"id\": ${note.id}, \"title\": ${json(note.title)}, " +
                "\"content\": ${json(note.content)}, \"color\": ${note.color}, " +
                "\"pinned\": ${note.pinned}, \"favorite\": ${note.favorite}, " +
                "\"archived\": ${note.archived}, \"updatedAt\": ${note.updatedAt}}"
        }.let { sb.append(it) }
        sb.append("\n  ],\n  \"tasks\": [")
        tasks.joinToString(",\n") { task ->
            "\n    {\"id\": ${task.id}, \"title\": ${json(task.title)}, " +
                "\"description\": ${json(task.description)}, \"done\": ${task.done}, " +
                "\"priority\": ${json(task.priority)}, " +
                "\"dueDate\": ${task.dueDate ?: "null"}}"
        }.let { sb.append(it) }
        sb.append("\n  ],\n  \"events\": [")
        events.joinToString(",\n") { event ->
            "\n    {\"id\": ${event.id}, \"title\": ${json(event.title)}, " +
                "\"description\": ${json(event.description)}, " +
                "\"startTime\": ${event.startTime}, \"endTime\": ${event.endTime}}"
        }.let { sb.append(it) }
        sb.append("\n  ]\n}\n")
        return sb.toString()
    }

    private fun json(value: String): String =
        "\"" + value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r") + "\""
}
