package com.example.assistant.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assistant.data.assistants
import com.example.assistant.data.defaultSettings
import com.example.assistant.ui.theme.AssistantTheme

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
        TextFieldDialog(
            value = settings.openAiKey,
            onConfirmation = { settingsViewModel.onOpenAiKeyChanged(it) },
            label = "OpenAI API key"
        )
        ItemPicker("Model", settingsViewModel.models, settings.selectedModel) {
            settingsViewModel.onModelSelected(it)
        }
        Divider()
        ItemPicker("Assistant type", assistants.map { it.name }, settings.selectedAssistant) {
            settingsViewModel.onAssistantSelected(it)
        }
        TextField(
            value = settings.aiPrompt,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldDialog(
    value: String,
    onConfirmation: (text: String) -> Unit,
    label: String
) {
    var isDialogShown by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = { isDialogShown = true }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column {
                Text(text = "OpenAI API key", style = MaterialTheme.typography.titleMedium)
                Text(text = "Enter your API key from https://platform.openai.com/api-keys", style = MaterialTheme.typography.bodyMedium)
            }
//            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "")
        }
    }

    if (!isDialogShown)
        return

    Dialog(onDismissRequest = { isDialogShown = false }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                var text by remember { mutableStateOf(value) }
                Text(
                    text = "Enter your API key from https://platform.openai.com/api-keys",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                TextField(
                    value = text,
                    label = { Text(label) },
                    onValueChange = { text = it },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { isDialogShown = false },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = { onConfirmation(text) },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TextFieldDialogPreview() {
    AssistantTheme {
        TextFieldDialog(value = "test", onConfirmation = {}, label = "OpenAI API key")
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