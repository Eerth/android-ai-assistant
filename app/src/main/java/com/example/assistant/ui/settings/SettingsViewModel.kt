package com.example.assistant.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.assistant.OPENAI_KEY
import com.example.assistant.SELECTED_ASSISTANT
import com.example.assistant.SELECTED_MODEL
import com.example.assistant.getSettingsFlow
import com.example.assistant.updatePrompt
import com.example.assistant.updateSetting
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SettingsViewModel(private val application: Application): AndroidViewModel(application) {

    companion object {
        const val TAG = "SettingsViewModel"
    }

    val settingsFlow = getSettingsFlow(application)

    fun onModelSelected(model: String) {
        viewModelScope.launch {
            updateSetting(application, SELECTED_MODEL, model)
        }
    }

    fun onAssistantSelected(assistant: String) {
        viewModelScope.launch {
            updateSetting(application, SELECTED_ASSISTANT, assistant)
        }
    }

    fun onPromptChanged(assistant: String, prompt: String) {
        runBlocking {
            updatePrompt(application, assistant, prompt)
        }
    }

    fun onOpenAiKeyChanged(key: String) {
        runBlocking {
            updateSetting(application, OPENAI_KEY, key)
        }
    }

}