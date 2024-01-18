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
            "system",
            "You are a helpful, pattern-following assistant that translates corporate jargon into plain English."
        ),
        Message(
            "system",
            "New synergies will help drive top-line growth."
        ),
        Message(
            "system",
            "Things working well together will increase revenue."
        ),
        Message(
            "system",
            "Let's circle back when we have more bandwidth to touch base on opportunities for increased leverage."
        ),
        Message(
            "system",
            "Let's talk later when we're less busy about how to do better."
        ),
        Message(
            "user",
            "This late pivot means we don't have time to boil the ocean for the client deliverable."
        )
    )

    @Test
    fun testNumTokensFromMessages() {
        val numTokens = Tokenizer.numTokensFromMessages(messages)

        assertEquals(115, numTokens)
    }

    @Test
    fun testNumTokensFromString() {
        val message = Message(role="assistant", content="This sudden change in direction means we don't have enough time to thoroughly address all aspects of the client deliverable.")
        val numTokens = Tokenizer.numTokensFromString(message.content)

        assertEquals(23, numTokens)
    }

    @Test
    fun testChatCompletion() = runTest {
        val chat = ChatCompletion("gpt-3.5-turbo", messages)
        val response = OpenAIService.retrofitService.postChatCompletion(
            "Bearer ${BuildConfig.OPENAI_API_KEY}",
            chat
        )

        assert(response.choices.isNotEmpty())
        assertEquals(115, response.usage?.promptTokens)
    }

}
