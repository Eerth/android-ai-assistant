package com.example.assistant.models

import com.example.assistant.BuildConfig
import com.example.assistant.data.assistants
import com.example.assistant.data.models

data class Settings(
    var selectedModel: Model,
    var selectedAssistant: String,
    var prompts: Map<String, String>,
    var openAiKey: String,
    var usageCounter: Double
) {
    companion object {
        fun withDefaults(
            selectedModel: Model? = null,
            selectedAssistant: String? = null,
            prompts: Map<String, String>? = null,
            openAiKey: String? = null,
            usageCounter: Double? = null
        ): Settings {
            return Settings(
                selectedModel ?: models.first(),
                selectedAssistant ?: assistants.first().name,
                assistants.associateBy ({ it.name }, { prompts?.get(it.name) ?: it.defaultPrompt }),
                openAiKey ?: BuildConfig.OPENAI_API_KEY,
                usageCounter ?: 0.0
            )
        }
    }
}
