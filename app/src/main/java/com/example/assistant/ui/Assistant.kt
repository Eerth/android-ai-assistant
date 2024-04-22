package com.example.assistant.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assistant.R
import com.example.assistant.data.MAX_USAGE
import com.example.assistant.ui.chat.Chat
import com.example.assistant.ui.settings.Settings
import com.example.assistant.ui.speak.Speak
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Assistant(assistantViewModel: AssistantViewModel = viewModel(factory = AssistantViewModel.Factory)) {
    val pagerState = rememberPagerState { 3 }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect {
            // Clear focus when switching pages
            focusManager.clearFocus()
        }
    }
    Scaffold(
        topBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = pagerState.currentPage == 0,
                    onClick = { coroutineScope.launch {
                        pagerState.animateScrollToPage(0)
                    }},
                    icon = { Icon(ImageVector.vectorResource(id = R.drawable.baseline_chat_24), "Chat") }
                )
                NavigationBarItem(
                    selected = pagerState.currentPage == 1,
                    onClick = { coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }},
                    icon = { Icon(ImageVector.vectorResource(id = R.drawable.baseline_mic_24), "Speak") }
                )
                NavigationBarItem(
                    selected = pagerState.currentPage == 2,
                    onClick = { coroutineScope.launch {
                        pagerState.animateScrollToPage(2)
                    }},
                    icon = { Icon(Icons.Filled.Settings, "Settings") }
                )
            }
        }
    ) { paddingValues ->
        val messages by assistantViewModel.messagesFlow.collectAsState(emptyList())
        val settings by assistantViewModel.settingsFlow.collectAsState(com.example.assistant.models.Settings())
        val inputEnabled = !assistantViewModel.gettingCompletion && settings.usageCounter <= MAX_USAGE
        HorizontalPager(state = pagerState, beyondBoundsPageCount = 1) { index ->
            when (index) {
                0 -> Chat(
                    messages = messages,
                    typingEnabled = inputEnabled,
                    onNewMessage = { assistantViewModel.onNewMessage(it, settings) },
                    onClearMessages = { assistantViewModel.clearMessages(settings.selectedAssistant) },
                    paddingValues = paddingValues
                )
                1 -> Speak(
                    isVisible = pagerState.currentPage == 1,
                    messages = messages,
                    recognitionEnabled = inputEnabled,
                    onSpeechRecognized = { assistantViewModel.onNewMessage(it, settings) },
                    paddingValues = paddingValues
                )
                2 -> Settings(
                    paddingValues = paddingValues
                )
            }
        }
    }
}
