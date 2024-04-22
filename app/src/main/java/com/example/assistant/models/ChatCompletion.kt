package com.example.assistant.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletion(
    val model: String,
    val messages: List<Map<String, String>>,
    val stream: Boolean = false
)