package com.notely.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Une liste personnalisée de tâches (ex. « Courses », « Travail »). */
@Entity(tableName = "task_lists")
data class TaskList(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)
