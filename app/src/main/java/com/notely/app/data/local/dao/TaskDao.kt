package com.notely.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.notely.app.data.local.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY done ASC, dueDate ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE done = 0 ORDER BY dueDate ASC, createdAt DESC")
    fun getRemainingTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE done = 1 ORDER BY updatedAt DESC")
    fun getDoneTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :start AND :end ORDER BY dueDate ASC")
    fun getTasksBetween(start: Long, end: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): Task?

    @Query("SELECT COUNT(*) FROM tasks WHERE done = 0")
    fun countRemaining(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks")
    fun countAll(): Flow<Int>

    @Insert
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}
