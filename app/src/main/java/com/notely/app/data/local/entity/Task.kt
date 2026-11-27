package com.notely.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Une tâche. priority vaut "basse", "moyenne" ou "haute".
 * dueDate et reminderAt sont des millisecondes (null si absents).
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val done: Boolean = false,
    val priority: String = "moyenne",
    val dueDate: Long? = null,
    val reminderAt: Long? = null,
    val categoryId: Long? = null,
    val listId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
