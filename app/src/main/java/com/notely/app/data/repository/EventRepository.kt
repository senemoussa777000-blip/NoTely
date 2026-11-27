package com.notely.app.data.repository

import android.content.Context
import com.notely.app.data.local.dao.EventDao
import com.notely.app.data.local.entity.Event
import com.notely.app.data.worker.ReminderWorker
import kotlinx.coroutines.flow.Flow

class EventRepository(
    private val context: Context,
    private val dao: EventDao
) {

    fun getAllEvents(): Flow<List<Event>> = dao.getAllEvents()

    fun getEventsBetween(start: Long, end: Long): Flow<List<Event>> =
        dao.getEventsBetween(start, end)

    fun getUpcomingEvents(from: Long, limit: Int = 5): Flow<List<Event>> =
        dao.getUpcomingEvents(from, limit)

    suspend fun getById(id: Long): Event? = dao.getById(id)

    suspend fun save(event: Event): Long {
        val now = System.currentTimeMillis()
        val id = if (event.id == 0L) {
            dao.insert(event.copy(createdAt = now, updatedAt = now))
        } else {
            dao.update(event.copy(updatedAt = now))
            event.id
        }
        val reminderAt = event.reminderAt
        if (reminderAt != null && reminderAt > now) {
            ReminderWorker.schedule(
                context = context,
                workName = "event_$id",
                title = event.title,
                text = "Événement à venir : ${event.title}",
                triggerAtMillis = reminderAt
            )
        }
        return id
    }

    suspend fun delete(event: Event) {
        ReminderWorker.cancel(context, "event_${event.id}")
        dao.delete(event)
    }
}
