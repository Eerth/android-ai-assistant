package com.example.assistant.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assistant.ChatViewModel

private const val TAG = "Settings"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(viewModel: ChatViewModel, paddingValues: PaddingValues) {
    LaunchedEffect(true) {
        viewModel.getModels()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        ItemPicker("Model", viewModel.models.map { it.id }, viewModel.selectedModel) {
            viewModel.selectedModel = it
        }
        ItemPicker("Assistant type", viewModel.assistants.map { it.name }, viewModel.selectedAssistant) { selectedItem ->
            viewModel.switchAssistant(selectedItem)
        }
        TextField(
            value = viewModel.aiPrompt,
            label = { Text(text = "Prompt") },
            onValueChange = { viewModel.aiPrompt = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemPicker(label: String, items: List<String>, selectedItem: String, onItemSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = Modifier.padding(16.dp)
    ) {
        TextField(
            value = selectedItem,
            label = { Text(text = label) },
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun ModelPickerPreview() {
    ItemPicker(
        label = "Model",
        items = listOf("gpt-3.5-turbo", "gpt-4-turbo"),
        selectedItem = "gpt-3.5-turbo",
        onItemSelected = {}
    )
}