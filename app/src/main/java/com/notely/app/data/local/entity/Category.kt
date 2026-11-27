package com.notely.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Une catégorie. type vaut "note", "tache" ou "evenement".
 * La couleur est stockée en ARGB (Long).
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val color: Long = 0xFF246BFD,
    val type: String = "note"
)
