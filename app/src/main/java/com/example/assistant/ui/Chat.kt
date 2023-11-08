package com.example.assistant.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assistant.R
import com.example.assistant.Recognizer
import com.example.assistant.models.Message
import com.example.assistant.ui.theme.AssistantTheme

val chatViewModel = ChatViewModel()

@Composable
fun Chat(recognizer: Recognizer) {
    Surface(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        MessageList(chatViewModel.messages)
        RecognizeButton(recognizer)
    }
}

@Composable
fun RecognizeButton(recognizer: Recognizer) {
    fun startRecognizer() {
        recognizer.start(object : Recognizer.OnResultListener {
            override fun onResult(text: String) {
                chatViewModel.editMessage(text)
                chatViewModel.getCompletion()
            }

            override fun onPartialResult(text: String) {
                chatViewModel.editMessage(text)
            }

            override fun onNoMatch() {
                chatViewModel.editMessage("?")
            }

            override fun onError(code: Int) {
                chatViewModel.editMessage("Error: $code")
            }
        })
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.padding(16.dp)
    ) {
        if (chatViewModel.gettingCompletion) {
            MicButton(enabled = false) {}
        }
        else {
            when (recognizer.recognizeState) {
                Recognizer.UNINITIALIZED -> {
                    MicButton(enabled = false) {}
                }

                Recognizer.INITIALIZED,
                Recognizer.FINISHED -> {
                    MicButton(onClick = {
                        chatViewModel.newMessage("...")
                        startRecognizer()
                    })
                }

                Recognizer.NO_MATCH,
                Recognizer.ERROR -> {
                    MicButton(onClick = {
                        chatViewModel.editMessage("...")
                        startRecognizer()
                    })
                }

                Recognizer.LISTENING -> {
                    MicButton(
                        enabled = false,
                        border = BorderStroke(
                            recognizer.rmsdBState.dp,
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

@Composable
fun MessageList(messages: List<Message>) {
    // Automatically scroll to bottom
    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size)
    }
    LazyColumn(state = listState) {
        items(messages) { message ->
            MessageCard(message)
        }
        item { Spacer(modifier = Modifier.height(64.dp)) }
    }
}

@Composable
fun MessageCard(msg: Message) {
    Column(modifier = Modifier.padding(all = 8.dp)) {
        Text(
            text = msg.role,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = msg.content)
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
            enabled = false,
            border = BorderStroke(5.dp, MaterialTheme.colorScheme.onPrimaryContainer)
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
fun MessageListPreview() {
    AssistantTheme {
        MessageList(
            listOf(
                Message("User", "Hello"),
                Message("Assistant", "Hello")
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MessagePreview() {
    AssistantTheme {
        MessageCard(Message("Assistant", "Hello"))
    }
}

@Preview(showBackground = true)
@Composable
fun ChatPreview() {
    AssistantTheme {
        Chat(Recognizer())
    }
}