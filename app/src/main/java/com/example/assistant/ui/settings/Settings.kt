package com.example.assistant.ui.settings

import android.util.Log
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assistant.data.assistants
import com.example.assistant.data.defaultSettings

private const val TAG = "Settings"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(paddingValues: PaddingValues, settingsViewModel: SettingsViewModel = viewModel()) {
    val settings by settingsViewModel.settingsFlow.collectAsState(defaultSettings)
    LaunchedEffect(true) {
        settingsViewModel.getModels()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        ItemPicker("Model", settingsViewModel.models, settings.selectedModel) {
            settingsViewModel.onModelSelected(it)
        }
        ItemPicker("Assistant type", assistants.map { it.name }, settings.selectedAssistant) {
            settingsViewModel.onAssistantSelected(it)
        }
        TextField(
            value = settings.selectedPrompt,
            label = { Text(text = "Prompt") },
            onValueChange = { settingsViewModel.onPromptChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemPicker(label: String, items: List<String>, selectedItem: String?, onItemSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = Modifier.padding(16.dp)
    ) {
        TextField(
            value = selectedItem ?: "",
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
fun SettingsPreview() {
    Settings(paddingValues = PaddingValues(0.dp))
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