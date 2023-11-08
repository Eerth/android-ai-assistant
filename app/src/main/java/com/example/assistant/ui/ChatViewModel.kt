package com.example.assistant.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assistant.models.ChatCompletion
import com.example.assistant.models.Message
import com.example.assistant.network.OpenAIService
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {

    companion object {
        const val TAG = "ChatViewModel"
    }

    val messages = mutableStateListOf(
        Message("system", "You are a helpful assistant.")
    )

    fun newMessage(text: String) {
        messages.add(Message("user", text))
    }

    fun editMessage(text: String) {
        messages[messages.lastIndex] = Message("user", text)
    }

    var gettingCompletion by mutableStateOf(false)
        private set

    fun getCompletion() {
        viewModelScope.launch {
            gettingCompletion = true
            messages.add(Message("assistant", "..."))
            val result = try {
                val chat = ChatCompletion("gpt-3.5-turbo", messages)
                val response = OpenAIService.retrofitService.postChatCompletions(chat)
                response.choices.first().message
            } catch (e: Exception) {
                Log.e(TAG, "OpenAI error", e)
                Message("assistant", "Error")
            }
            messages[messages.lastIndex] = result
            gettingCompletion = false
        }
    }

}