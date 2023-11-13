package com.example.assistant

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.assistant.models.ChatCompletion
import com.example.assistant.models.Message
import com.example.assistant.network.OpenAIService
import kotlinx.coroutines.launch

class ChatViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        const val TAG = "ChatViewModel"
        const val OPEN_AI_CONTEXT = "You are a helpful assistant."
    }

    val recognizer = Recognizer(application)

    val messages = mutableStateListOf(
        Message("assistant", "How can I help you?")
    )

    fun onNewMessage(text: String) {
        messages.add(Message("user", text))
    }

    fun onEditLastMessage(text: String) {
        messages[messages.lastIndex] = Message("user", text)
    }

    fun startRecognizer() {
        recognizer.start(object : Recognizer.OnResultListener {
            override fun onResult(text: String) {
                onEditLastMessage(text)
                getCompletion()
            }

            override fun onPartialResult(text: String) {
                onEditLastMessage(text)
            }

            override fun onNoMatch() {
                onEditLastMessage("?")
            }

            override fun onError(code: Int) {
                onEditLastMessage("Error: $code")
            }
        })
    }

    var gettingCompletion by mutableStateOf(false)
        private set

    fun getCompletion() {
        viewModelScope.launch {
            gettingCompletion = true
            val result = try {
                val chat = ChatCompletion("gpt-3.5-turbo", messages.withContext(OPEN_AI_CONTEXT))
                Log.d(TAG, "Sending chat: $chat")
                val response = OpenAIService.retrofitService.postChatCompletions(chat)
                response.choices.first().message
            } catch (e: Exception) {
                Log.e(TAG, "OpenAI error", e)
                Message("assistant", "Error")
            }
            messages.add(result)
            gettingCompletion = false
        }
    }

    private fun List<Message>.withContext(context: String): List<Message> {
        return listOf(Message("system", context)) + this
    }
}