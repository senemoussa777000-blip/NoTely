package com.notely.app.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notely.app.data.local.entity.Note
import com.notely.app.data.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NoteFilter { TOUTES, FAVORIS, ARCHIVES }
enum class NoteSort { RECENTES, TITRE }

class NotesViewModel(private val repository: NoteRepository) : ViewModel() {

    val query = MutableStateFlow("")
    val filter = MutableStateFlow(NoteFilter.TOUTES)
    val sort = MutableStateFlow(NoteSort.RECENTES)

    val notes: StateFlow<List<Note>> = combine(query, filter, sort) { q, f, s ->
        Triple(q, f, s)
    }.flatMapLatest { (q, f, s) ->
        val base: Flow<List<Note>> = when {
            q.isNotBlank() -> repository.searchNotes(q.trim())
            f == NoteFilter.FAVORIS -> repository.getFavoriteNotes()
            f == NoteFilter.ARCHIVES -> repository.getArchivedNotes()
            else -> repository.getActiveNotes()
        }
        base.map { list ->
            when (s) {
                NoteSort.TITRE -> list.sortedBy { it.title.lowercase() }
                NoteSort.RECENTES -> list
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setQuery(value: String) { query.value = value }

    fun setFilter(value: NoteFilter) { filter.value = value }

    fun setSort(value: NoteSort) { sort.value = value }

    fun togglePinned(note: Note) = viewModelScope.launch {
        repository.setPinned(note, !note.pinned)
    }

    fun toggleFavorite(note: Note) = viewModelScope.launch {
        repository.setFavorite(note, !note.favorite)
    }

    fun toggleArchived(note: Note) = viewModelScope.launch {
        repository.setArchived(note, !note.archived)
    }

    fun delete(note: Note) = viewModelScope.launch { repository.delete(note) }
}
