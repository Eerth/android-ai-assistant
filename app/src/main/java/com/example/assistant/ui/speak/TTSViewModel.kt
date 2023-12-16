package com.example.assistant.ui.speak

import android.app.Application
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TTSViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        const val TAG = "TTSViewModel"
    }

    private val tts = TextToSpeech(application) { status ->
        if (status == TextToSpeech.SUCCESS) {
            Log.d(TAG, "TTS engine is successfully initialized.")
        }
    }

    private var spokenText = ""

    suspend fun speak(text: String) = withContext(Dispatchers.IO) {
        // Remove text that's already spoken
        val newText = text.substringAfter(spokenText)

        // Get first full sentence
        val regex = Regex("([^.:;!?]+[.:;!?])")
        val fullSentence = regex.find(newText)?.value ?: ""

        // Save spoken text
        spokenText += fullSentence

        tts.speak(fullSentence, TextToSpeech.QUEUE_ADD, null, null)
    }

    fun clear() {
        spokenText = ""
        tts.stop()
    }

}