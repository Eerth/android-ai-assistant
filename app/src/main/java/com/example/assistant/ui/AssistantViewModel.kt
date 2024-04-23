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
import com.example.assistant.getSettingsFlow
import com.example.assistant.models.ChatCompletion
import com.example.assistant.models.ChatResponse
import com.example.assistant.models.Message
import com.example.assistant.models.Model
import com.example.assistant.models.Settings
import com.example.assistant.network.OpenAIService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json


private val json = Json { ignoreUnknownKeys = true }

@OptIn(ExperimentalCoroutinesApi::class)
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

    val settingsFlow = getSettingsFlow(application)
    val messagesFlow = settingsFlow
        .distinctUntilChanged { oldSettings, newSettings ->
            oldSettings.selectedAssistant == newSettings.selectedAssistant
        }
        .flatMapLatest { settings ->
            messagesRepository.getAllMessagesFlow(settings.selectedAssistant)
        }
    var gettingCompletion by mutableStateOf(false)

    private var getCompletionJob: Job? = null

    fun clearMessages(assistant: String) {
        viewModelScope.launch {
            getCompletionJob?.cancelAndJoin()
            messagesRepository.deleteAllMessages(assistant)
            addFirstMessage(assistant)
        }
    }

    fun onNewMessage(text: String, settings: Settings) {
        getCompletionJob = viewModelScope.launch {
            messagesRepository.insertMessage(
                Message(assistant = settings.selectedAssistant, role = "user", content = text)
            )
            getCompletion(settings)
        }
    }

    suspend fun addFirstMessage(assistant: String) {
        Log.d(TAG, "Adding first message for $assistant")
        val firstMessage = assistants
            .find { a -> a.name == assistant }
            ?.firstMessage

        if (firstMessage != null) {
            messagesRepository.insertMessage(
                Message(assistant = assistant, role = "assistant", content = firstMessage)
            )
        }
    }

    private suspend fun getCompletion(settings: Settings) = withContext(Dispatchers.Default) {
        gettingCompletion = true

        val messagesToBeSent = messagesRepository
            .getAllMessages(settings.selectedAssistant)
            .reduceMessages()
            .addContext(settings.selectedAssistant, settings.aiPrompt)
            .map { mapOf("role" to it.role, "content" to it.content) }

        val completionMessage = Message(assistant = settings.selectedAssistant, role = "assistant", content = "")
        completionMessage.id = messagesRepository.insertMessage(completionMessage)

        addInputUsage(settings.selectedModel, messagesToBeSent.map { it["content"] ?: "" })

        try {
            val chat = ChatCompletion(settings.selectedModel.name, messagesToBeSent, true)
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

            addOutputUsage(settings.selectedModel, completionMessage.content)
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

    private fun List<Message>.addContext(assistant: String, context: String): List<Message> {
        return listOf(Message(assistant = assistant, role = "system", content = context)) + this
    }

    private fun Message.addContent(content: String) {
        this.content += content
    }

    private suspend fun addInputUsage(model: Model, messages: List<String>) {
        val promptTokens = Tokenizer.numTokensFromMessages(messages)
        val cost = promptTokens * model.inputPrice
        addCost(application, cost)
    }

    private suspend fun addOutputUsage(model: Model, text: String) {
        val completionTokens = Tokenizer.numTokensFromString(text)
        val cost = completionTokens * model.outputPrice
        addCost(application, cost)
    }

}