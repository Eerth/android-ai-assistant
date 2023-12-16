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
    "AI_PROMPT" to stringPreferencesKey("AI_PROMPT"),
    "OPENAI_KEY" to stringPreferencesKey("OPENAI_KEY")
)

fun getSettingsFlow(context: Context, defaultSettings: Settings): Flow<Settings> {
    return context.dataStore.data
        .map { preferences ->
            Settings(
                preferences[preferencesKeys["SELECTED_MODEL"]!!] ?: defaultSettings.selectedModel,
                preferences[preferencesKeys["SELECTED_ASSISTANT"]!!] ?: defaultSettings.selectedAssistant,
                preferences[preferencesKeys["AI_PROMPT"]!!] ?: defaultSettings.aiPrompt,
                preferences[preferencesKeys["OPENAI_KEY"]!!] ?: defaultSettings.openAiKey
            )
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