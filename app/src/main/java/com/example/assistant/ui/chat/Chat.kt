package com.example.assistant.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assistant.models.Message
import com.example.assistant.ui.theme.AssistantTheme
import kotlinx.coroutines.launch

private const val TAG = "Chat"

@Composable
fun Chat(
    messages: List<Message>,
    typingEnabled: Boolean,
    onNewMessage: (String) -> Unit,
    onClearMessages: () -> Unit,
    paddingValues: PaddingValues
) {
    Column {
        Box(modifier = Modifier.weight(1f)) {
            MessageList(messages, onClearMessages, paddingValues)
        }
        Type(enabled = typingEnabled, onClick = onNewMessage)
    }
}

@Composable
fun MessageList(messages: List<Message>, onClearMessages: () -> Unit, paddingValues: PaddingValues) {
    // Automatically scroll to bottom
    val listState = rememberLazyListState()
    LaunchedEffect(messages) {
        if (listState.canScrollForward && !listState.isScrollInProgress) {
            listState.scrollToItem(
                messages.size,
                -listState.layoutInfo.viewportSize.height + 32
            )
        }
    }
    val scope = rememberCoroutineScope()
    Column {
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                val horizontalAlignment =
                    if (message.role == "user")
                        Alignment.TopEnd
                    else
                        Alignment.TopStart

                Box(contentAlignment = horizontalAlignment, modifier = Modifier.fillMaxWidth()) {
                    MessageCard(message)
                }
            }
            if (messages.size > 1) {
                item {
                    TextButton(
                        onClick = onClearMessages,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    ) {
                        Icon(Icons.Outlined.Delete, "Delete")
                        Text(text = "Clear chat")
                    }
                }
            }
        }
    }
}

@Composable
fun MessageCard(msg: Message) {
    val cardColors =
        if (msg.role == "user")
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.secondaryContainer

    Card(colors = CardDefaults.cardColors(containerColor = cardColors)) {
        Column(modifier = Modifier.padding(PaddingValues(10.dp, 8.dp))) {
            Text(text = msg.content)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatPreview() {
    AssistantTheme {
        Chat(
            messages = listOf(
                Message("user", "Hello"),
                Message("assistant", "Hello")
            ),
            typingEnabled = true,
            onNewMessage = {},
            onClearMessages = {},
            paddingValues = PaddingValues(0.dp)
        )
    }
}

@Preview
@Composable
fun MessageListPreview() {
    MessageList(
        listOf(
            Message("user", "Hello"),
            Message("assistant", "Hello")
        ),
        onClearMessages = {},
        PaddingValues(0.dp)
    )
}

@Preview
@Composable
fun MessagePreview() {
    MessageCard(Message("Assistant", "Hello"))
}
