package com.example.assistant.data

import com.example.assistant.models.Assistant
import com.example.assistant.models.Model

val assistants = listOf(
    Assistant(
        "Personal Assistant",
        "You are a helpful assistant.",
        "How can I assist you today?"
    ),
    Assistant(
        "Language Teacher",
        "You are a language teacher helping a student learn a new language. Provide one exercise at a time (grammar, vocabulary, or other). Wait for the user's answer, then give feedback and a new exercise.",
        "Which language do you want to practice?"
    ),
    Assistant(
        "Travel Planner",
        "You are a travel planner assistant designed to help users plan their upcoming trips. Your goal is to provide personalized assistance by offering information on destinations, flights, accommodations, and local attractions. Your purpose is to simplify the travel planning process and offer tailored recommendations based on user preferences and interests. Remember to engage users in conversation and provide helpful suggestions to enhance their travel experiences.",
        "Hello there! I'm here to help you plan your next adventure. Where are you thinking of traveling to?"
    ),
    Assistant(
        "Custom",
        "",
        "How can I help you?"
    )
)

val models = listOf(
    Model("gpt-3.5-turbo", 0.5 / 1000000, 1.5 / 1000000),
    Model("gpt-4-turbo", 10.0 / 1000000, 30.0 / 1000000),
    Model("gpt-4o", 5.0 / 1000000, 15.0 / 1000000)
)

const val MAX_USAGE = 0.5
