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

class Recognizer(context: Context) {

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

    interface OnResultListener {
        fun onResult(text: String)
        fun onPartialResult(text: String)
        fun onNoMatch()
        fun onError(code: Int)
    }

    var onResultListener: OnResultListener? = null

    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
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
                    when (code) {
                        SpeechRecognizer.ERROR_NO_MATCH -> {
                            recognizeState = NO_MATCH
                            onResultListener?.onNoMatch()
                        }

                        else -> {
                            recognizeState = ERROR
                            onResultListener?.onError(code)
                        }
                    }
                }

                override fun onResults(results: Bundle?) {
                    Log.d(TAG, "onResults, $results")

                    val matches =
                        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    Log.i(TAG, "Final results: $matches")
                    if (!matches.isNullOrEmpty()) {
                        onResultListener?.onResult(matches[0])
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val matches =
                        partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    Log.i(TAG, "Partial results: $matches")
                    if (!matches.isNullOrEmpty()) {
                        onResultListener?.onPartialResult(matches[0])
                    }
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

    fun start(onResultListener: OnResultListener) {
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
        onResultListener = null
        recognizeState = UNINITIALIZED
    }

}