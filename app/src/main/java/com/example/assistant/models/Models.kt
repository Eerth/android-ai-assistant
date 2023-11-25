package com.example.assistant.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Models(
    @SerialName("object")
    val type: String,
    val data: List<Model>
) {
    fun getGptModels(): List<Model> {
        return this.data.filter { it.id.startsWith("gpt") }.sortedBy { it.id }
    }
}

@Serializable
data class Model(
    val id: String,
    @SerialName("object")
    val type: String? = null,
    val created: Int? = null,
    @SerialName("owned_by")
    val ownedBy: String? = null
)
