package com.example.assistant.data

import com.example.assistant.BuildConfig
import com.example.assistant.models.AssistantType
import com.example.assistant.models.Settings

val assistants = listOf(
    AssistantType(
        "Personal Assistant",
        "You are a helpful assistant.",
        "How can I assist you today?"
    ),
    AssistantType(
        "Language Teacher",
        "You are an English speaking language teacher, helping someone learn a new language. Give one grammar or vocabulary exercise at the time and wait for the user to answer. After giving feedback on the answer, give a new exercise.",
        "Which language do you want to practice?"
    ),
    AssistantType(
        "Travel Planner",
        "You are a travel planner assistant designed to help users plan their upcoming trips. Your goal is to provide personalized assistance by offering information on destinations, flights, accommodations, and local attractions. Your purpose is to simplify the travel planning process and offer tailored recommendations based on user preferences and interests. Remember to engage users in conversation and provide helpful suggestions to enhance their travel experiences.",
        "Hello there! I'm here to help you plan your next adventure. Where are you thinking of traveling to?"
    )
)

val defaultSettings = Settings(
    "gpt-4-1106-preview",
    assistants.first().name,
    assistants.first().prompt,
    BuildConfig.OPENAI_API_KEY,
    0.0
)

const val MAX_USAGE = 0.5