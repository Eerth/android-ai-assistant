package com.example.assistant

import com.example.assistant.models.ChatCompletion
import com.example.assistant.models.Message
import com.example.assistant.network.OpenAIService
import com.example.assistant.ui.Tokenizer
import kotlinx.coroutines.test.runTest
import org.junit.Test

import org.junit.Assert.*

class OpenAIUnitTest {

    private val messages = listOf(
        Message(
            role = "system",
            content = "You are a helpful, pattern-following assistant that translates corporate jargon into plain English."
        ),
        Message(
            role = "system",
            content = "New synergies will help drive top-line growth."
        ),
        Message(
            role = "system",
            content = "Things working well together will increase revenue."
        ),
        Message(
            role = "system",
            content = "Let's circle back when we have more bandwidth to touch base on opportunities for increased leverage."
        ),
        Message(
            role = "system",
            content = "Let's talk later when we're less busy about how to do better."
        ),
        Message(
            role = "user",
            content = "This late pivot means we don't have time to boil the ocean for the client deliverable."
        )
    )

    @Test
    fun testNumTokensFromMessages() {
        val numTokens = Tokenizer.numTokensFromMessages(messages.map { it.content })

        assertEquals(115, numTokens)
    }

    @Test
    fun testNumTokensFromString() {
        val content = "This sudden change in direction means we don't have enough time to thoroughly address all aspects of the client deliverable."
        val numTokens = Tokenizer.numTokensFromString(content)

        assertEquals(23, numTokens)
    }

    @Test
    fun testChatCompletion() = runTest {
        val chat = ChatCompletion("gpt-3.5-turbo", messages.map { mapOf("role" to it.role, "content" to it.content) })
        val response = OpenAIService.retrofitService.postChatCompletion(
            "Bearer ${BuildConfig.OPENAI_API_KEY}",
            chat
        )

        assert(response.choices.isNotEmpty())
        assertEquals(115, response.usage?.promptTokens)
    }

}
