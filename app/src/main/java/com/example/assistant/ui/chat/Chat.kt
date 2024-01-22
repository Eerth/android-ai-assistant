package com.example.assistant.ui.chat

import android.view.ViewTreeObserver
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assistant.models.Message
import com.example.assistant.ui.theme.AssistantTheme

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
    LaunchedEffect(listState.canScrollForward) {
        if (listState.canScrollForward && !listState.isScrollInProgress) {
            listState.animateScrollToItem(messages.size)
        }
    }
    val isKeyboardOpen by isKeyboardOpenState()
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
            // Show clear button when keyboard is closed
            if (messages.size > 1 && !isKeyboardOpen) {
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
fun isKeyboardOpenState(): State<Boolean> {
    val isKeyboardOpen = remember { mutableStateOf(false) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            isKeyboardOpen.value = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }
    return isKeyboardOpen
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
