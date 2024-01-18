package com.example.assistant.ui

import com.example.assistant.models.Message
import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.EncodingType

object Tokenizer {

    fun numTokensFromMessages(messages: List<Message>): Int {
        val registry = Encodings.newDefaultEncodingRegistry()
        val encoding = registry.getEncoding(EncodingType.CL100K_BASE)
        return messages.sumOf { encoding.countTokens(it.content) } + 4 * messages.size + 3
    }

    fun numTokensFromString(text: String): Int {
        val registry = Encodings.newDefaultEncodingRegistry()
        val encoding = registry.getEncoding(EncodingType.CL100K_BASE)
        return encoding.countTokens(text)
    }

}