package com.example.assistant.ui.speak

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assistant.R
import com.example.assistant.ui.theme.AssistantTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assistant.models.Message

const val TAG = "Speak"

@Composable
fun Speak(
    isVisible: Boolean,
    messages: List<Message>,
    recognitionEnabled: Boolean,
    onSpeechRecognized: (String) -> Unit,
    paddingValues: PaddingValues,
    ttsViewModel: TTSViewModel = viewModel()
) {
    val assistantMessages = messages.filter { it.role == "assistant" }
    DisposableEffect(assistantMessages.size) {
        ttsViewModel.clear()
        onDispose {
            ttsViewModel.clear()
        }
    }
    val lastMessageText = assistantMessages.lastOrNull()?.content ?: ""
    LaunchedEffect(lastMessageText) {
        if (isVisible && lastMessageText.isNotBlank()) {
            ttsViewModel.speak(lastMessageText)
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(lastMessageText)
            RecognizeButton(recognitionEnabled, { ttsViewModel.clear() }, onSpeechRecognized)
        }
    }
}

@Composable
fun RecognizeButton(
    enabled: Boolean,
    onStartRecognizing: () -> Unit,
    onSpeechRecognized: (String) -> Unit,
    recognizerViewModel: RecognizerViewModel = viewModel()
) {
    DisposableEffect(true) {
        onDispose {
            recognizerViewModel.stopRecognizing()
        }
    }
    if (!enabled) {
        MicButton(enabled = false) {}
    } else {
        when (recognizerViewModel.recognizeState) {
            RecognizerViewModel.UNINITIALIZED -> {
                MicButton(enabled = false) {}
            }

            RecognizerViewModel.LISTENING -> {
                MicButton(
                    enabled = false,
                    border = BorderStroke(
                        recognizerViewModel.rmsdBState.dp,
                        MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {}
            }

            else -> {
                MicButton(onClick = {
                    onStartRecognizing()
                    recognizerViewModel.startRecognizing(onSpeechRecognized)
                })
            }
        }
    }
}

@Composable
fun MicButton(enabled: Boolean = true, border: BorderStroke? = null, onClick: () -> Unit) {
    Button(enabled = enabled, border = border, onClick = onClick, modifier = Modifier.scale(1.3f)) {
        Icon(ImageVector.vectorResource(id = R.drawable.baseline_mic_24), "microphone")
    }
}

@Preview
@Composable
fun ButtonPreview() {
    AssistantTheme {
        MicButton {}
    }
}

@Preview
@Composable
fun ButtonPreview2() {
    AssistantTheme {
        MicButton(
            enabled = false
        ) {}
    }
}