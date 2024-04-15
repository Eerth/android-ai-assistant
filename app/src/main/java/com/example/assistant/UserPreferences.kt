package com.example.assistant

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.assistant.data.models
import com.example.assistant.models.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val TAG = "UserPreferences"

val Context.dataStore by preferencesDataStore("user_preferences")

val SELECTED_MODEL = stringPreferencesKey("selected_model")
val SELECTED_ASSISTANT = stringPreferencesKey("selected_assistant")
val AI_PROMPT = stringPreferencesKey("ai_prompt")
val OPENAI_KEY = stringPreferencesKey("openai_key")
val USAGE_COUNTER = doublePreferencesKey("usage_counter")

fun getSettingsFlow(context: Context): Flow<Settings> {
    val defaultSettings = Settings()
    return context.dataStore.data
        .map { preferences ->
            Settings(
                models.firstOrNull { it.name == preferences[SELECTED_MODEL] } ?: defaultSettings.selectedModel,
                preferences[SELECTED_ASSISTANT] ?: defaultSettings.selectedAssistant,
                preferences[AI_PROMPT] ?: defaultSettings.aiPrompt,
                preferences[OPENAI_KEY] ?: defaultSettings.openAiKey,
                preferences[USAGE_COUNTER] ?: defaultSettings.usageCounter
            )
        }
}

suspend fun updateSetting(context: Context, key: Preferences.Key<String>, value: String) {
    Log.d(TAG, "Updating setting $key to $value")
    context.dataStore.edit { settings ->
        settings[key] = value
    }
}

suspend fun addCost(context: Context, cost: Double) {
    context.dataStore.edit { settings ->
        val currentCounterValue = settings[USAGE_COUNTER] ?: 0.0
        settings[USAGE_COUNTER] = currentCounterValue + cost
        Log.d(TAG, "Usage: $${settings[USAGE_COUNTER]}")
    }
}