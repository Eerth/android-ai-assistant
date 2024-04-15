package com.example.assistant.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.assistant.AI_PROMPT
import com.example.assistant.OPENAI_KEY
import com.example.assistant.SELECTED_ASSISTANT
import com.example.assistant.SELECTED_MODEL
import com.example.assistant.data.assistants
import com.example.assistant.getSettingsFlow
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

            assistants
                .find { assistantType ->  assistantType.name == assistant }
                ?.prompt
                ?.let {
                    updateSetting(application, AI_PROMPT, it)
                }
        }
    }

    fun onPromptChanged(text: String) {
        runBlocking {
            updateSetting(application, AI_PROMPT, text)
        }
    }

    fun onOpenAiKeyChanged(key: String) {
        runBlocking {
            updateSetting(application, OPENAI_KEY, key)
        }
    }

}