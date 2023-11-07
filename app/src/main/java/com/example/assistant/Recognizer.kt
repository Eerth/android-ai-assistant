package com.example.assistant

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Recognizer {

    companion object {
        private const val TAG = "Recognizer"
        const val UNINITIALIZED = 0
        const val INITIALIZED = 1
        const val LISTENING = 2
        const val FINISHED = 3
        const val ERROR = 4
    }

    private var speechRecognizer: SpeechRecognizer? = null
    var state by mutableStateOf(UNINITIALIZED)
        private set

    var onResultListener: ((String) -> Unit)? = null

    fun create(context: Context) {
        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "onReadyForSpeech")
                state = LISTENING
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech")
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.d(TAG, "onEndOfSpeech")
                state = FINISHED
            }

            override fun onError(error: Int) {
                Log.e(TAG, "error $error")
                state = ERROR
            }

            override fun onResults(results: Bundle?) {
                Log.d(TAG, "onResults, $results")

                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                Log.i(TAG, "Final results: $matches")
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    onResultListener?.let { it(recognizedText) }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                Log.i(TAG, "Partial results: $matches")
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}

            override fun onLanguageDetection(results: Bundle) {
                super.onLanguageDetection(results)
                if (Build.VERSION.SDK_INT >= 34) {
                    val language = results.getString(SpeechRecognizer.DETECTED_LANGUAGE)
                    Log.d(TAG, "Detected language $language")
                }
            }
        })
        state = INITIALIZED
    }

    fun start(onResultListener: (String) -> Unit) {
        Log.d(TAG, "start")
        this.onResultListener = onResultListener
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        if (Build.VERSION.SDK_INT >= 34) {
            intent.putExtra(RecognizerIntent.EXTRA_ENABLE_LANGUAGE_SWITCH, RecognizerIntent.LANGUAGE_SWITCH_BALANCED)
        }
        speechRecognizer?.startListening(intent)
    }

    fun stop() {
        Log.d(TAG, "stop")
        onResultListener = null
        speechRecognizer?.stopListening()
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        onResultListener = null
        state = UNINITIALIZED
    }

}