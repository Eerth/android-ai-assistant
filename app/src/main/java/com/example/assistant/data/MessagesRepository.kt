package com.example.assistant.data

import android.util.Log
import com.example.assistant.models.Message
import kotlinx.coroutines.flow.Flow

class MessagesRepository(private val messageDao: MessageDao) {

    companion object {
        const val TAG = "MessagesRepository"
    }

    fun getAllMessagesFlow(): Flow<List<Message>> = messageDao.getAllMessagesFlow()

    suspend fun getAllMessages(): List<Message> = messageDao.getAllMessages()

    suspend fun insertMessage(message: Message): Long = messageDao.insert(message)

    suspend fun deleteMessage(message: Message) = messageDao.delete(message)

    suspend fun deleteAllMessages() = messageDao.deleteAllMessages()

    suspend fun updateMessage(message: Message) {
        Log.d(TAG, "Updating message: $message")
        messageDao.update(message)
    }
    
}