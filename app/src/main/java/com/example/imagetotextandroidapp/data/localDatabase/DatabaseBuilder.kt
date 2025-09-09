package com.example.imagetotextandroidapp.data.localDatabase


object DatabaseBuilder {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: android.content.Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = androidx.room.Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "text_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
