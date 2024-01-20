package com.example.assistant

import android.app.Application
import com.example.assistant.data.MessageDatabase
import com.example.assistant.data.MessagesRepository

class AssistantApplication: Application() {

    val messagesRepository: MessagesRepository by lazy {
        MessagesRepository(MessageDatabase.getDatabase(this).messageDao())
    }

}