package com.example.assistant.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import com.example.assistant.ChatViewModel
import com.example.assistant.R
import com.example.assistant.ui.chat.Chat
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun Assistant(chatViewModel: ChatViewModel) {
    val pagerState = rememberPagerState()
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
//                NavigationBarItem(
//                    selected = pagerState.currentPage == 1,
//                    onClick = { coroutineScope.launch {
//                        pagerState.animateScrollToPage(1)
//                    }},
//                    icon = { Icon(ImageVector.vectorResource(id = R.drawable.baseline_mic_24), "Speak") }
//                )
                NavigationBarItem(
                    selected = pagerState.currentPage == 1,
                    onClick = { coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }},
                    icon = { Icon(Icons.Filled.Settings, "Settings") }
                )
            }
        }
    ) { paddingValues ->
        HorizontalPager(pageCount = 2, state = pagerState, beyondBoundsPageCount = 1) { index ->
            when (index) {
                0 -> Chat(chatViewModel, paddingValues)
//                1 -> Speak(chatViewModel)
                1 -> Settings(chatViewModel, paddingValues)
            }
        }
    }
}