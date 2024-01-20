package com.example.assistant.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "messages")
data class Message(
    val role: String,
    var content: String,
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
)
