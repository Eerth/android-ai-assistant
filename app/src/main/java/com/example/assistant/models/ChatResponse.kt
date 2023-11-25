package com.example.assistant.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val id: String,
    @SerialName("object")
    val type: String,
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val index: Int,
    val message: Message? = null,
    val delta: Delta? = null,
    @SerialName("finish_reason")
    val finishReason: String? = null
)

@Serializable
data class Delta(
    val content: String? = null
)