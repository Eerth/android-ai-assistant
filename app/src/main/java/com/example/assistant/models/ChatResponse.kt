package com.example.assistant.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val id: String,
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val index: Int,
    val message: Message
)