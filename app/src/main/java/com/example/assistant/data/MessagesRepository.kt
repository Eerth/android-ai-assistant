package com.example.assistant.data

import android.util.Log
import com.example.assistant.models.Message
import kotlinx.coroutines.flow.Flow

class MessagesRepository(private val messageDao: MessageDao) {

    companion object {
        const val TAG = "MessagesRepository"
    }

    fun getAllMessagesFlow(assistant: String): Flow<List<Message>> {
        Log.d(TAG, "getAllMessagesFlow: $assistant")
        return messageDao.getAllMessagesFlow(assistant)
    }

    suspend fun getAllMessages(assistant: String): List<Message> = messageDao.getAllMessages(assistant)

    suspend fun insertMessage(message: Message): Long = messageDao.insert(message)

    suspend fun deleteMessage(message: Message) = messageDao.delete(message)

    suspend fun deleteAllMessages(assistant: String) = messageDao.deleteAllMessages(assistant)

    suspend fun updateMessage(message: Message) = messageDao.update(message)
    
}