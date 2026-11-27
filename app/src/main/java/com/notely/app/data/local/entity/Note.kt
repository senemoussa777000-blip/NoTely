package com.notely.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Une note. La couleur est stockée en ARGB (Long).
 * createdAt / updatedAt sont des millisecondes depuis l'époque Unix.
 */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val color: Long = 0xFFF5F1E8, // Cream par défaut
    val categoryId: Long? = null,
    val pinned: Boolean = false,
    val favorite: Boolean = false,
    val archived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
