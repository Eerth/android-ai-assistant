package com.example.assistant

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.assistant.models.AssistantType
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

class ChatViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        const val TAG = "ChatViewModel"
    }

    val recognizer = Recognizer(application)

    var models by mutableStateOf(emptyList<Model>())

    val assistants = listOf(
        AssistantType(
            "LanguageTeacher",
            application.getString(R.string.language_teacher_prompt),
            application.getString(R.string.language_teacher_first_question)
        ),
        AssistantType(
            "Personal Assistant",
            application.getString(R.string.personal_assistant_prompt),
            application.getString(R.string.personal_assistant_first_question)
        ),
    )

    var aiPrompt by preferenceAsState(
        application,
        PreferencesKeys.AI_PROMPT,
        application.getString(R.string.language_teacher_prompt)
    )

    var selectedModel by preferenceAsState(
        application,
        PreferencesKeys.SELECTED_MODEL,
        application.getString(R.string.default_model)
    )

    var selectedAssistant by preferenceAsState(
        application,
        PreferencesKeys.SELECTED_ASSISTANT,
        "LanguageTeacher"
    )

    val messages = mutableStateListOf(
        Message("assistant", application.getString(R.string.language_teacher_first_question))
    )

    fun onNewMessage(text: String) {
        messages.add(Message("user", text))
    }

    fun onEditLastMessage(text: String) {
        messages[messages.lastIndex] = Message("user", text)
    }

    fun switchAssistant(newAssistant: String) {
        selectedAssistant = newAssistant
        messages.clear()
        assistants.find { it.name == newAssistant }?.let { assistant ->
            aiPrompt = assistant.prompt
            assistant.firstMessage?.let { messages.add(Message("assistant", it)) }
        }
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

    fun getModels() {
        viewModelScope.launch {
            val response = OpenAIService.retrofitService.getModels()
            Log.d(TAG, "Get models: ${response.getGptModels()}")
            models = response.getGptModels()
        }
    }

    fun getCompletion() {
        viewModelScope.launch {
            gettingCompletion = true
            messages.add(Message("assistant", ""))
            try {
                val chat = ChatCompletion(selectedModel, messages.addContext(aiPrompt), true)
                Log.d(TAG, "Sending chat: $chat")
                val response = OpenAIService.retrofitService.postChatCompletions(chat)
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
            } catch (e: Exception) {
                Log.e(TAG, "OpenAI error", e)
                messages[messages.lastIndex] = Message("assistant", "Error")
            }
            gettingCompletion = false
        }
    }

    private fun MutableList<Message>.addContent(content: String) {
        this[this.lastIndex] = Message(this.last().role, this.last().content + content)
    }

    private fun List<Message>.addContext(context: String): List<Message> {
        return listOf(Message("system", context)) + this
    }

}