package com.example.assistant.ui

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.assistant.addCost
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.assistant.data.assistants
import com.example.assistant.data.defaultSettings
import com.example.assistant.getSettingsFlow
import com.example.assistant.models.ChatCompletion
import com.example.assistant.models.ChatResponse
import com.example.assistant.models.Message
import com.example.assistant.models.Model
import com.example.assistant.network.OpenAIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json


private val json = Json { ignoreUnknownKeys = true }

class AssistantViewModel(private val application: Application): AndroidViewModel(application) {

    companion object {
        const val TAG = "ChatViewModel"
    }

    var settings by mutableStateOf(defaultSettings)
    var models by mutableStateOf(emptyList<Model>())
    val messages = mutableStateListOf<Message>()
    var gettingCompletion by mutableStateOf(false)

    init {
        viewModelScope.launch {
            getSettingsFlow(application, defaultSettings).collect { newSettings ->
                if (newSettings.selectedAssistant != settings.selectedAssistant) {
                    messages.clear()
                }

                if (messages.isEmpty()) {
                    addFirstMessage(newSettings.selectedAssistant)
                }

                settings = newSettings
            }
        }
    }

    fun onNewMessage(text: String) {
        messages.add(Message("user", text))
        viewModelScope.launch {
            getCompletion()
        }
    }

    private fun addFirstMessage(selectedAssistant: String) {
        val firstMessage = assistants
            .find { assistantType ->  assistantType.name == selectedAssistant }
            ?.firstMessage

        if (firstMessage != null)
            messages.add(Message("assistant", firstMessage))
    }

    private suspend fun getCompletion() = withContext(Dispatchers.Default) {
        gettingCompletion = true
        messages.add(Message("assistant", ""))
        try {
            val messagesToBeSent = messages.reduceMessages().addContext(settings.aiPrompt)

            addInputUsage(messagesToBeSent)

            val chat = ChatCompletion(settings.selectedModel, messagesToBeSent, true)
            Log.d(TAG, "Sending chat: $chat")
            val response = OpenAIService.retrofitService.streamChatCompletion("Bearer ${settings.openAiKey}", chat)

            val input = response.byteStream().bufferedReader()
            while (isActive) {
                val line = withContext(Dispatchers.IO) {
                    input.readLine()
                }
                if (line.isNullOrBlank())
                    continue
                val data = line.substringAfter("data:").trim()
                if (data == "[DONE]")
                    break
                val choice = json.decodeFromString<ChatResponse>(data)
                val content = choice.choices.first().delta?.content
                messages.addContent(content ?: "")
            }
            Log.d(TAG, "Finished getting completion: ${messages.last()}")

            addOutputUsage(messages.last().content)
        } catch (e: Exception) {
            Log.e(TAG, "OpenAI error", e)
            messages[messages.lastIndex] = Message("assistant", "Error")
        }
        gettingCompletion = false
    }

    private fun List<Message>.reduceMessages(): List<Message> {
        return this.takeLast(9)
    }

    private fun List<Message>.addContext(context: String): List<Message> {
        return listOf(Message("system", context)) + this
    }

    private fun MutableList<Message>.addContent(content: String) {
        this[this.lastIndex] = Message(this.last().role, this.last().content + content)
    }

    private suspend fun addInputUsage(messages: List<Message>) {
        val promptTokens = Tokenizer.numTokensFromMessages(messages)
        val cost = 0.01 * promptTokens / 1000
        addCost(application, cost)
    }

    private suspend fun addOutputUsage(text: String) {
        val completionTokens = Tokenizer.numTokensFromString(text)
        val cost = 0.03 * completionTokens / 1000
        addCost(application, cost)
    }

}