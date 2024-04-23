package com.example.assistant.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import com.example.assistant.data.MAX_USAGE
import com.example.assistant.data.assistants
import com.example.assistant.data.models
import com.example.assistant.ui.theme.AssistantTheme

private const val TAG = "Settings"

@Composable
fun Settings(paddingValues: PaddingValues, settingsViewModel: SettingsViewModel = viewModel()) {
    val settings by settingsViewModel.settingsFlow.collectAsState(com.example.assistant.models.Settings.withDefaults())
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
        ItemPicker("Model", models.map { it.name }, settings.selectedModel.name) {
            settingsViewModel.onModelSelected(it)
        }
        HorizontalDivider()
        ItemPicker("Assistant type", assistants.map { it.name }, settings.selectedAssistant) {
            settingsViewModel.onAssistantSelected(it)
        }
        TextField(
            value = settings.prompts[settings.selectedAssistant] ?: "",
            label = { Text(text = "Prompt") },
            onValueChange = { settingsViewModel.onPromptChanged(settings.selectedAssistant, it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        HorizontalDivider()
        Usage(settings.usageCounter)
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
            items.filter { item -> item != selectedItem }.forEach { item ->
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
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "OpenAI API key", style = MaterialTheme.typography.titleMedium)
                Text(text = "Enter your API key from https://platform.openai.com/api-keys", style = MaterialTheme.typography.bodyMedium)
            }
            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "", modifier = Modifier.padding(16.dp))
        }
    }
    if (isDialogShown)
        ApiKeyDialog(value, label, onConfirmation) { isDialogShown = false }
}

@Composable
fun ApiKeyDialog(value: String, label: String, onConfirmation: (String) -> Unit, onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp),
            ) {
                var text by remember { mutableStateOf(value) }
                Text(
                    text = "Enter your API key from https://platform.openai.com/api-keys",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(16.dp)
                )
                TextField(
                    value = text,
                    label = { Text(label) },
                    onValueChange = { text = it },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = onDismissRequest,
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

@Composable
fun Usage(usage: Double) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Usage: $" + String.format("%.2f", usage), style = MaterialTheme.typography.titleMedium)
        LinearProgressIndicator(
            (usage / MAX_USAGE).toFloat(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview
@Composable
fun UsagePreview() {
    AssistantTheme {
        Usage(usage = 0.05)
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
fun ApiKeyDialogPreview() {
    AssistantTheme {
        ApiKeyDialog(value = "test", label = "OpenAI API key", onConfirmation = {}, onDismissRequest = {})
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