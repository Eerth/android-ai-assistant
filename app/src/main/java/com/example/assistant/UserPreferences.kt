package com.example.assistant

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.assistant.models.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val TAG = "UserPreferences"

val Context.dataStore by preferencesDataStore("user_preferences")

val preferencesKeys = mapOf(
    "SELECTED_MODEL" to stringPreferencesKey("SELECTED_MODEL"),
    "SELECTED_ASSISTANT" to stringPreferencesKey("SELECTED_ASSISTANT"),
    "AI_PROMPT" to stringPreferencesKey("AI_PROMPT")
)

fun getSettingsFlow(context: Context, defaultSettings: Settings): Flow<Settings> {
    return context.dataStore.data
        .map { preferences ->
            val model = preferences[preferencesKeys["SELECTED_MODEL"]!!] ?: defaultSettings.selectedModel
            val assistant = preferences[preferencesKeys["SELECTED_ASSISTANT"]!!] ?: defaultSettings.selectedAssistant
            val prompt = preferences[preferencesKeys["AI_PROMPT"]!!] ?: defaultSettings.selectedPrompt

            // Map your preferences to Settings object
            Settings(model, assistant, prompt)
        }
}

suspend fun updateSetting(context: Context, key: String, value: String) {
    Log.d(TAG, "Updating setting $key to $value")
    context.dataStore.edit { settings ->
        preferencesKeys[key]?.let {
            settings[it] = value
        }
    }
}