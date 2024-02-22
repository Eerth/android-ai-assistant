package com.example.assistant.models

import android.util.Log
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Models(
    @SerialName("object")
    val type: String,
    val data: List<Model>
) {
    companion object {
        const val TAG = "Models"
    }

    private fun getGptModels(): List<Model> {
        return this.data.filter { it.id.startsWith("gpt") }.sortedBy { it.id }
    }

    fun getSelectedModels(): List<Model> {
        return try {
            listOf(
                this.data.first { it.id == "gpt-3.5-turbo" },
                this.data.firstOrNull { it.id == "gpt-4-turbo-preview" }
                    ?: this.data.first { it.id == "gpt-4" }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception getting selected models", e)
            getGptModels()
        }
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
