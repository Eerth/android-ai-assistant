package com.example.assistant.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletion(
    val model: String,
    val messages: List<Message>
)

@Serializable
data class Message(
    val role: String,
    val content: String
)
