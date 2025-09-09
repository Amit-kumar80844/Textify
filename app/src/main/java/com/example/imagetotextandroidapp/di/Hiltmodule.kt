package com.example.imagetotextandroidapp.di

import android.content.Context
import androidx.room.Room
import com.example.imagetotextandroidapp.data.localDatabase.AppDatabase
import com.example.imagetotextandroidapp.data.localDatabase.TextDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "text_database"
        ).build()
    }

    @Provides
    fun provideDao(appDatabase: AppDatabase): TextDao {
        return appDatabase.textDao()
    }
}
