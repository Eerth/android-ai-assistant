package com.example.assistant.models

data class Settings(
    var selectedModel: String,
    var selectedAssistant: String,
    var aiPrompt: String,
    var openAiKey: String,
    var usageCounter: Double
)
