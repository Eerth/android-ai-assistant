package com.example.assistant.ui.speak

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assistant.ChatViewModel
import com.example.assistant.R
import com.example.assistant.Recognizer
import com.example.assistant.ui.theme.AssistantTheme

@Composable
fun Speak(chatViewModel: ChatViewModel) {
    Surface(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        RecognizeButton(chatViewModel)
    }
}

@Composable
fun RecognizeButton(chatViewModel: ChatViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        if (chatViewModel.gettingCompletion) {
            MicButton(enabled = false) {}
        }
        else {
            when (chatViewModel.recognizer.recognizeState) {
                Recognizer.UNINITIALIZED -> {
                    MicButton(enabled = false) {}
                }

                Recognizer.INITIALIZED,
                Recognizer.FINISHED -> {
                    MicButton(onClick = {
                        chatViewModel.onNewMessage("...")
                        chatViewModel.startRecognizer()
                    })
                }

                Recognizer.NO_MATCH,
                Recognizer.ERROR -> {
                    MicButton(onClick = {
                        chatViewModel.onEditLastMessage("...")
                        chatViewModel.startRecognizer()
                    })
                }

                Recognizer.LISTENING -> {
                    MicButton(
                        enabled = false,
                        border = BorderStroke(
                            chatViewModel.recognizer.rmsdBState.dp,
                            MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {}
                }
            }
        }
    }
}

@Composable
fun MicButton(enabled: Boolean = true, border: BorderStroke? = null, onClick: () -> Unit) {
    Button(enabled = enabled, border = border, onClick = onClick) {
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