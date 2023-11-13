package com.example.assistant.ui.chat

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Type(enabled: Boolean = true, onClick: (text: String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Row(modifier = Modifier.padding(PaddingValues(16.dp, 8.dp, 16.dp, 16.dp)).height(IntrinsicSize.Max)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            enabled = enabled,
            onClick = {
                onClick(text)
                text = ""
            },
            modifier = Modifier.fillMaxHeight()
        ) {
            Icon(Icons.Filled.Send, "Send")
        }
    }
}

@Preview
@Composable
fun TypePreview() {
    Type {}
}

@Preview
@Composable
fun DisabledTypePreview() {
    Type(enabled = false) {}
}