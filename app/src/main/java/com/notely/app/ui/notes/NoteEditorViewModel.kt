package com.notely.app.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notely.app.data.local.entity.Note
import com.notely.app.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoteEditorViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _note = MutableStateFlow<Note?>(null)
    val note: StateFlow<Note?> = _note

    fun load(noteId: Long) {
        if (noteId == -1L) {
            _note.value = Note(title = "", content = "")
            return
        }
        viewModelScope.launch {
            _note.value = repository.getById(noteId)
        }
    }

    fun save(title: String, content: String, color: Long) {
        val current = _note.value ?: return
        if (title.isBlank() && content.isBlank()) return
        viewModelScope.launch {
            repository.save(current.copy(title = title, content = content, color = color))
        }
    }

    fun delete() {
        val current = _note.value ?: return
        if (current.id == 0L) return
        viewModelScope.launch { repository.delete(current) }
    }
}
