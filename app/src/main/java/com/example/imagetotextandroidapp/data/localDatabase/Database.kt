package com.example.imagetotextandroidapp.data.localDatabase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TextEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun textDao(): TextDao
}
