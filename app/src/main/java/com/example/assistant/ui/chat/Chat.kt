package com.example.assistant.ui.chat

import android.util.Log
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assistant.ChatViewModel
import com.example.assistant.models.Message
import com.example.assistant.ui.theme.AssistantTheme

private const val TAG = "Chat"

@Composable
fun Chat(viewModel: ChatViewModel, paddingValues: PaddingValues) {
    Column {
        Box(modifier = Modifier.weight(1f)) {
            MessageList(viewModel.messages, paddingValues)
        }
        Type(enabled = !viewModel.gettingCompletion, onClick = { text ->
            viewModel.onNewMessage(text)
            viewModel.getCompletion()
        })
    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val view = LocalView.current
    var isImeVisible by remember { mutableStateOf(false) }

    DisposableEffect(LocalWindowInfo.current) {
        val listener = ViewTreeObserver.OnPreDrawListener {
            isImeVisible = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) == true
            true
        }
        view.viewTreeObserver.addOnPreDrawListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnPreDrawListener(listener)
        }
    }
    return rememberUpdatedState(isImeVisible)
}

@Composable
fun MessageList(messages: List<Message>, paddingValues: PaddingValues) {
    // Automatically scroll to bottom
    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size)
    }
    val isKeyboardOpen by keyboardAsState()
    LaunchedEffect(isKeyboardOpen) {
        Log.d(TAG, "Keyboard open: $isKeyboardOpen")
        if (isKeyboardOpen)
            listState.animateScrollToItem(messages.size)
    }
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
fun MessageListPreview() {
    AssistantTheme {
        MessageList(
            listOf(
                Message("user", "Hello"),
                Message("assistant", "Hello")
            ),
            PaddingValues(0.dp)
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
