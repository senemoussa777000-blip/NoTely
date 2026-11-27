package com.notely.app.data.repository

import android.content.Context
import com.notely.app.data.local.dao.TaskDao
import com.notely.app.data.local.entity.Task
import com.notely.app.data.worker.ReminderWorker
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val context: Context,
    private val dao: TaskDao
) {

    fun getAllTasks(): Flow<List<Task>> = dao.getAllTasks()

    fun getRemainingTasks(): Flow<List<Task>> = dao.getRemainingTasks()

    fun getTasksBetween(start: Long, end: Long): Flow<List<Task>> = dao.getTasksBetween(start, end)

    fun countRemaining(): Flow<Int> = dao.countRemaining()

    fun countAll(): Flow<Int> = dao.countAll()

    suspend fun getById(id: Long): Task? = dao.getById(id)

    suspend fun save(task: Task): Long {
        val now = System.currentTimeMillis()
        val id = if (task.id == 0L) {
            dao.insert(task.copy(createdAt = now, updatedAt = now))
        } else {
            dao.update(task.copy(updatedAt = now))
            task.id
        }
        scheduleReminder(task.copy(id = id))
        return id
    }

    suspend fun delete(task: Task) {
        ReminderWorker.cancel(context, "task_${task.id}")
        dao.delete(task)
    }

    suspend fun setDone(task: Task, done: Boolean) {
        val updated = task.copy(done = done, updatedAt = System.currentTimeMillis())
        dao.update(updated)
        if (done) {
            ReminderWorker.cancel(context, "task_${task.id}")
        } else {
            scheduleReminder(updated)
        }
    }

    private fun scheduleReminder(task: Task) {
        val reminderAt = task.reminderAt ?: return
        if (task.done || reminderAt <= System.currentTimeMillis()) return
        ReminderWorker.schedule(
            context = context,
            workName = "task_${task.id}",
            title = task.title,
            text = "Rappel : ${task.title}",
            triggerAtMillis = reminderAt
        )
    }
}
