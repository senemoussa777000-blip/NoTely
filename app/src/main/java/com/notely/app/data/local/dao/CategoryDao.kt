package com.notely.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.notely.app.data.local.entity.Category
import com.notely.app.data.local.entity.TaskList
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name ASC")
    fun getCategoriesByType(type: String): Flow<List<Category>>

    @Insert
    suspend fun insert(category: Category): Long

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT * FROM task_lists ORDER BY name ASC")
    fun getAllTaskLists(): Flow<List<TaskList>>

    @Insert
    suspend fun insertTaskList(list: TaskList): Long

    @Delete
    suspend fun deleteTaskList(list: TaskList)
}
