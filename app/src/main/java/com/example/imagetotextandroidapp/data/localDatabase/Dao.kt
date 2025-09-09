package com.example.imagetotextandroidapp.data.localDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TextDao {

    @Insert
    suspend fun insertText(entity: TextEntity)

    @Query("SELECT * FROM text_table ORDER BY timestamp DESC")
    suspend fun getAllText(): List<TextEntity>

    @Query("DELETE FROM text_table WHERE id = :id")
    suspend fun deleteTextById(id: Int)
}
