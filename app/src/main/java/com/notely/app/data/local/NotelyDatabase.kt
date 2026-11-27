package com.notely.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.notely.app.data.local.dao.CategoryDao
import com.notely.app.data.local.dao.EventDao
import com.notely.app.data.local.dao.NoteDao
import com.notely.app.data.local.dao.TaskDao
import com.notely.app.data.local.entity.Category
import com.notely.app.data.local.entity.Event
import com.notely.app.data.local.entity.Note
import com.notely.app.data.local.entity.Task
import com.notely.app.data.local.entity.TaskList

/**
 * Base Room de NOTELY. Version 1.
 * Aucune donnée fictive : la base est vide au premier lancement.
 */
@Database(
    entities = [Note::class, Task::class, Event::class, Category::class, TaskList::class],
    version = 1,
    exportSchema = false
)
abstract class NotelyDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao
    abstract fun eventDao(): EventDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: NotelyDatabase? = null

        fun getInstance(context: Context): NotelyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotelyDatabase::class.java,
                    "notely.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
