package com.example.assistant.ui.settings

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.assistant.data.assistants
import com.example.assistant.data.defaultSettings
import com.example.assistant.getSettingsFlow
import com.example.assistant.network.OpenAIService
import com.example.assistant.updateSetting
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SettingsViewModel(private val application: Application): AndroidViewModel(application) {

    companion object {
        const val TAG = "SettingsViewModel"
    }

    val settingsFlow = getSettingsFlow(application, defaultSettings)

    fun onModelSelected(model: String) {
        viewModelScope.launch {
            updateSetting(application, "SELECTED_MODEL", model)
        }
    }

    fun onAssistantSelected(assistant: String) {
        viewModelScope.launch {
            updateSetting(application, "SELECTED_ASSISTANT", assistant)

            assistants
                .find { assistantType ->  assistantType.name == assistant }
                ?.prompt
                ?.let {
                    updateSetting(application, "AI_PROMPT", it)
                }
        }
    }

    fun onPromptChanged(text: String) {
        runBlocking {
            updateSetting(application, "AI_PROMPT", text)
        }
    }

    var models by mutableStateOf(emptyList<String>())

    fun getModels() {
        viewModelScope.launch {
            val response = OpenAIService.retrofitService.getModels()
            models = response.getGptModels().map { it.id }
        }
    }

}