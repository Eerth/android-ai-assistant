package com.example.assistant.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.assistant.models.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert
    suspend fun insert(message: Message): Long

    @Update
    suspend fun update(message: Message)

    @Delete
    suspend fun delete(message: Message)

    @Query("DELETE FROM messages WHERE assistant = :assistant")
    suspend fun deleteAllMessages(assistant: String)

    @Query("SELECT * FROM messages WHERE assistant = :assistant")
    suspend fun getAllMessages(assistant: String): List<Message>

    @Query("SELECT * FROM messages WHERE assistant = :assistant")
    fun getAllMessagesFlow(assistant: String): Flow<List<Message>>
}