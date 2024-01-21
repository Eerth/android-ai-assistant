package com.example.assistant.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.assistant.AssistantApplication
import com.example.assistant.addCost
import com.example.assistant.data.assistants
import com.example.assistant.data.defaultSettings
import com.example.assistant.getSettingsFlow
import com.example.assistant.models.ChatCompletion
import com.example.assistant.models.ChatResponse
import com.example.assistant.models.Message
import com.example.assistant.models.Model
import com.example.assistant.network.OpenAIService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json


private val json = Json { ignoreUnknownKeys = true }

class AssistantViewModel(private val application: AssistantApplication): AndroidViewModel(application) {

    companion object {
        const val TAG = "ChatViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY]) as AssistantApplication
                return AssistantViewModel((application )) as T
            }
        }
    }

    private val messagesRepository = application.messagesRepository

    val messagesFlow = messagesRepository.getAllMessagesFlow()
    var settings by mutableStateOf(defaultSettings)
    var models by mutableStateOf(emptyList<Model>())
    var gettingCompletion by mutableStateOf(false)

    private var getCompletionJob: Job? = null

    init {
        viewModelScope.launch {
            getSettingsFlow(application, defaultSettings).collect { newSettings ->
                if (newSettings.selectedAssistant != settings.selectedAssistant) {
                    messagesRepository.deleteAllMessages()
                }

                if (messagesRepository.getAllMessages().isEmpty()) {
                    addFirstMessage(newSettings.selectedAssistant)
                }

                settings = newSettings
            }
        }
    }

    fun clearMessages() {
        viewModelScope.launch {
            getCompletionJob?.cancelAndJoin()
            messagesRepository.deleteAllMessages()
            addFirstMessage(settings.selectedAssistant)
        }
    }

    fun onNewMessage(text: String) {
        getCompletionJob = viewModelScope.launch {
            messagesRepository.insertMessage(Message("user", text))
            getCompletion()
        }
    }

    private fun addFirstMessage(selectedAssistant: String) {
        val firstMessage = assistants
            .find { assistantType -> assistantType.name == selectedAssistant }
            ?.firstMessage

        if (firstMessage != null) {
            viewModelScope.launch {
                messagesRepository.insertMessage(Message("assistant", firstMessage))
            }
        }
    }

    private suspend fun getCompletion() = withContext(Dispatchers.Default) {
        gettingCompletion = true

        val messagesToBeSent = messagesRepository
            .getAllMessages()
            .reduceMessages()
            .removeIds()
            .addContext(settings.aiPrompt)

        val completionMessage = Message("assistant", "")
        completionMessage.id = messagesRepository.insertMessage(completionMessage)

        addInputUsage(messagesToBeSent)

        try {
            val chat = ChatCompletion(settings.selectedModel, messagesToBeSent, true)
            Log.d(TAG, "Sending chat: $chat")
            val response = OpenAIService.retrofitService.streamChatCompletion(
                "Bearer ${settings.openAiKey}",
                chat
            )

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
                completionMessage.addContent(content ?: "")
                messagesRepository.updateMessage(completionMessage)
            }
            Log.d(TAG, "Finished getting completion: $completionMessage")

            addOutputUsage(completionMessage.content)
        } catch (e: CancellationException) {
            Log.d(TAG, "Getting completion cancelled")
        } catch (e: Exception) {
            Log.e(TAG, "OpenAI error", e)

            completionMessage.content = "Error"
            messagesRepository.updateMessage(completionMessage)
        }

        gettingCompletion = false
    }

    private fun List<Message>.reduceMessages(): List<Message> {
        return this.takeLast(9)
    }

    private fun List<Message>.removeIds(): List<Message> {
        return this.map { Message(it.role, it.content) }
    }

    private fun List<Message>.addContext(context: String): List<Message> {
        return listOf(Message("system", context)) + this
    }

    private fun Message.addContent(content: String) {
        this.content = this.content + content
    }

    private suspend fun addInputUsage(messages: List<Message>) {
        val promptTokens = Tokenizer.numTokensFromMessages(messages)
        val price = when (settings.selectedModel) {
            "gpt-4" -> 0.03
            "gpt-3.5-turbo-1106" -> 0.001
            "gpt-3.5-turbo" -> 0.001
            else -> 0.01
        }
        val cost = price * promptTokens / 1000
        addCost(application, cost)
    }

    private suspend fun addOutputUsage(text: String) {
        val completionTokens = Tokenizer.numTokensFromString(text)
        val price = when (settings.selectedModel) {
            "gpt-4" -> 0.06
            "gpt-3.5-turbo-1106" -> 0.002
            "gpt-3.5-turbo" -> 0.002
            else -> 0.03
        }
        val cost = price * completionTokens / 1000
        addCost(application, cost)
    }

}