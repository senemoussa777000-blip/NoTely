package com.notely.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Un événement du calendrier. startTime / endTime en millisecondes.
 */
@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val startTime: Long,
    val endTime: Long,
    val reminderAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
