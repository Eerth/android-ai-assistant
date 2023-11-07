package com.example.assistant.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assistant.models.ChatCompletion
import com.example.assistant.models.Message
import com.example.assistant.network.OpenAIService
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {

    val messages = mutableStateListOf(
        Message("system", "You are a helpful assistant.")
    )

    fun addResult(recognizedText: String) {
        messages.add(Message("user", recognizedText))
    }

    fun getCompletion() {
        viewModelScope.launch {
            messages.add(Message("assistant", "..."))
            val chat = ChatCompletion("gpt-3.5-turbo", messages)
            val result = OpenAIService.retrofitService.postChatCompletions(chat)
            messages[messages.lastIndex] = result.choices.first().message
        }
    }

}