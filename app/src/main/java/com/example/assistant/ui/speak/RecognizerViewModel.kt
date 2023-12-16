package com.example.assistant.ui.speak

import android.app.Application
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
import androidx.lifecycle.AndroidViewModel

class RecognizerViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "Recognizer"
        const val UNINITIALIZED = 0
        const val INITIALIZED = 1
        const val LISTENING = 2
        const val FINISHED = 3
        const val NO_MATCH = 4
        const val ERROR = 5
    }

    var recognizeState by mutableStateOf(UNINITIALIZED)
        private set

    var rmsdBState by mutableStateOf(0f)
        private set

    var onResultListener: ((String) -> Unit)? = null

    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(application)
        .apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Log.d(TAG, "onReadyForSpeech")
                    recognizeState = LISTENING
                }

                override fun onBeginningOfSpeech() {
                    Log.d(TAG, "onBeginningOfSpeech")
                }

                override fun onRmsChanged(rmsdB: Float) {
                    rmsdBState = (rmsdBState * 3 + rmsdB) / 4
                }

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    Log.d(TAG, "onEndOfSpeech")
                    recognizeState = FINISHED
                }

                override fun onError(code: Int) {
                    Log.e(TAG, "error $code")
                    recognizeState = if (code == SpeechRecognizer.ERROR_NO_MATCH) {
                        NO_MATCH
                    } else {
                        ERROR
                    }
                }

                override fun onResults(results: Bundle?) {
                    Log.d(TAG, "onResults, $results")

                    val matches =
                        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    Log.i(TAG, "Final results: $matches")
                    if (!matches.isNullOrEmpty()) {
                        onResultListener?.invoke(matches[0])
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val matches =
                        partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
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
            recognizeState = INITIALIZED
        }

    fun startRecognizing(onResultListener: (String) -> Unit) {
        Log.d(TAG, "startRecognizing")
        this.onResultListener = onResultListener
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            if (Build.VERSION.SDK_INT >= 34) {
                putExtra(
                    RecognizerIntent.EXTRA_ENABLE_LANGUAGE_SWITCH,
                    RecognizerIntent.LANGUAGE_SWITCH_BALANCED
                )
            }
        }
        speechRecognizer?.startListening(intent)
    }

    fun stopRecognizing() {
        Log.d(TAG, "stop")
        onResultListener = null
        if (recognizeState == LISTENING)
            speechRecognizer?.stopListening()
    }

    fun destroy() {
        speechRecognizer?.destroy()
        onResultListener = null
        recognizeState = UNINITIALIZED
    }

}