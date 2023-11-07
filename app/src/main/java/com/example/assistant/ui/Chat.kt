package com.example.assistant.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
        recognizer.start() { result ->
            chatViewModel.addResult(result)
            chatViewModel.getCompletion()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.padding(16.dp)
    ) {
        when (recognizer.state) {
            Recognizer.UNINITIALIZED -> {
                FilledButton("Loading...", enabled = false) {}
            }
            Recognizer.INITIALIZED, Recognizer.FINISHED -> {
                FilledButton(icon = R.drawable.baseline_mic_24) {
                    startRecognizer()
                }
            }
            Recognizer.LISTENING -> {
                FilledButton("Listening...", enabled = false) {}
            }
            Recognizer.ERROR -> {
                FilledButton("Error") {
                    startRecognizer()
                }
            }
        }
    }
}

@Composable
fun FilledButton(text: String? = null, icon: Int? = null, enabled: Boolean = true, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = enabled) {
        icon?.let { Icon(ImageVector.vectorResource(id = it), "microphone") }
        text?.let { Text(it) }
    }
}

@Composable
fun MessageList(messages: List<Message>) {
    LazyColumn() {
        items(messages) { message ->
            MessageCard(message)
        }
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
        FilledButton("Click") {}
    }
}

@Preview
@Composable
fun ButtonPreview2() {
    AssistantTheme {
        FilledButton(icon = R.drawable.baseline_mic_24) {}
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