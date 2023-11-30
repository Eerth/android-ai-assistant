package com.example.assistant.ui.chat

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
fun MessageList(messages: List<Message>, paddingValues: PaddingValues) {
    // Automatically scroll to bottom
    val listState = rememberLazyListState()
    LaunchedEffect(listState.canScrollForward) {
        if (listState.canScrollForward && !listState.isScrollInProgress) {
            listState.animateScrollToItem(messages.size)
        }
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
