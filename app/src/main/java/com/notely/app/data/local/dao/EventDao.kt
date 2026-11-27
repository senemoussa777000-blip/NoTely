package com.notely.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.notely.app.data.local.entity.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM events ORDER BY startTime ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE startTime BETWEEN :start AND :end ORDER BY startTime ASC")
    fun getEventsBetween(start: Long, end: Long): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE startTime >= :from ORDER BY startTime ASC LIMIT :limit")
    fun getUpcomingEvents(from: Long, limit: Int): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getById(id: Long): Event?

    @Insert
    suspend fun insert(event: Event): Long

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)
}
