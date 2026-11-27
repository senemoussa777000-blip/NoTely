package com.notely.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notely.app.data.local.entity.Event
import com.notely.app.data.local.entity.Note
import com.notely.app.data.repository.EventRepository
import com.notely.app.data.repository.NoteRepository
import com.notely.app.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val notesCount: Int = 0,
    val remainingTasks: Int = 0,
    val totalTasks: Int = 0,
    val upcomingEvents: List<Event> = emptyList(),
    val recentNotes: List<Note> = emptyList()
) {
    /** Progression des tâches du jour : 0f à 1f. */
    val taskProgress: Float
        get() = if (totalTasks == 0) 0f else (totalTasks - remainingTasks).toFloat() / totalTasks
}

class HomeViewModel(
    noteRepository: NoteRepository,
    taskRepository: TaskRepository,
    eventRepository: EventRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        noteRepository.countActiveNotes(),
        taskRepository.countRemaining(),
        taskRepository.countAll(),
        eventRepository.getUpcomingEvents(System.currentTimeMillis(), 5),
        noteRepository.getRecentNotes(5)
    ) { notesCount, remaining, total, events, recent ->
        HomeUiState(
            notesCount = notesCount,
            remainingTasks = remaining,
            totalTasks = total,
            upcomingEvents = events,
            recentNotes = recent
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())
}
