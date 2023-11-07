package com.example.assistant

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.assistant.ui.Chat
import com.example.assistant.ui.theme.AssistantTheme

class MainActivity : ComponentActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val recognizer = Recognizer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recognizer.create(this)

        setContent {
            AssistantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Chat(recognizer)
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Log.i(TAG, "Permission granted: $isGranted")
    }

    override fun onStart() {
        super.onStart()
        requestPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
    }

    override fun onStop() {
        super.onStop()
        recognizer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        recognizer.destroy()
    }

}

