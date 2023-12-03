package com.example.assistant.data

import com.example.assistant.models.AssistantType
import com.example.assistant.models.Settings

val assistants = listOf(
    AssistantType(
        "LanguageTeacher",
        "You are an English speaking language teacher, helping someone learn a new language. Give one grammar or vocabulary exercise at the time and wait for the user to answer. After giving feedback on the answer, give a new exercise.",
        "Which language do you want to practice?"
    ),
    AssistantType(
        "Personal Assistant",
        "You are a helpful assistant.",
        "How can I assist you today?"
    ),
)

val defaultSettings = Settings(
    "gpt-3.5-turbo",
    assistants.first().name,
    assistants.first().prompt
)