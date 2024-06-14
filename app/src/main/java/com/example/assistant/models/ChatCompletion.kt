package com.example.assistant.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletion(
    val model: String,
    val messages: List<Map<String, String>>,
    val stream: Boolean = false,
    val stream_options: StreamOptions? = null
)

@Serializable
data class StreamOptions(
    val include_usage: Boolean? = null
)