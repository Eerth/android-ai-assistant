package com.example.assistant

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

const val TAG = "UserPreferences"

val Context.dataStore by preferencesDataStore("user_preferences")

//@Composable
fun <T> preferenceAsState(
    context: Context,
    key: Preferences.Key<T>,
    defaultValue: T,
): MutableState<T> {
    return object : MutableState<T> {
        var state by mutableStateOf(defaultValue)
        override var value: T
            get() {
                state = getUserPreference(context, key, defaultValue)
                return state
            }
            set(value) {
                state = value
                setUserPreference(context, key, value)
            }

        override fun component1() = value
        override fun component2(): (T) -> Unit = { value = it }
    }
}

object PreferencesKeys {
    val AI_PROMPT = stringPreferencesKey("AI_PROMPT")
    val SELECTED_MODEL = stringPreferencesKey("SELECTED_MODEL")
    val SELECTED_ASSISTANT = stringPreferencesKey("SELECTED_ASSISTANT")
}

fun <T> getUserPreference(context: Context, key: Preferences.Key<T>, defaultValue: T): T {
    return runBlocking {
        context.dataStore.data.map { data ->
            data[key] ?: defaultValue
        }.first()
    }
}

fun <T> setUserPreference(context: Context, key: Preferences.Key<T>, value: T) {
    runBlocking {
        context.dataStore.edit {
            it[key] = value
        }
    }
}