package com.example.assistant.models

import kotlinx.serialization.Serializable

@Serializable
data class Model(
    val name: String,
    val inputPrice: Double, // Price per token
    val outputPrice: Double
)
