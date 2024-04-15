package com.example.assistant.models

import com.example.assistant.BuildConfig
import com.example.assistant.data.assistants
import com.example.assistant.data.models

data class Settings(
    var selectedModel: Model = models.first(),
    var selectedAssistant: String = assistants.first().name,
    var aiPrompt: String = assistants.first().prompt,
    var openAiKey: String = BuildConfig.OPENAI_API_KEY,
    var usageCounter: Double = 0.0
)
