package com.notely.app.data.repository

import com.notely.app.data.local.dao.NoteDao
import com.notely.app.data.local.entity.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteDao) {

    fun getActiveNotes(): Flow<List<Note>> = dao.getActiveNotes()

    fun getArchivedNotes(): Flow<List<Note>> = dao.getArchivedNotes()

    fun getFavoriteNotes(): Flow<List<Note>> = dao.getFavoriteNotes()

    fun getRecentNotes(limit: Int = 5): Flow<List<Note>> = dao.getRecentNotes(limit)

    fun searchNotes(query: String): Flow<List<Note>> = dao.searchNotes(query)

    fun countActiveNotes(): Flow<Int> = dao.countActiveNotes()

    suspend fun getById(id: Long): Note? = dao.getById(id)

    suspend fun save(note: Note): Long {
        val now = System.currentTimeMillis()
        return if (note.id == 0L) {
            dao.insert(note.copy(createdAt = now, updatedAt = now))
        } else {
            dao.update(note.copy(updatedAt = now))
            note.id
        }
    }

    suspend fun delete(note: Note) = dao.delete(note)

    suspend fun setPinned(note: Note, pinned: Boolean) =
        dao.update(note.copy(pinned = pinned, updatedAt = System.currentTimeMillis()))

    suspend fun setFavorite(note: Note, favorite: Boolean) =
        dao.update(note.copy(favorite = favorite, updatedAt = System.currentTimeMillis()))

    suspend fun setArchived(note: Note, archived: Boolean) =
        dao.update(note.copy(archived = archived, updatedAt = System.currentTimeMillis()))
}
