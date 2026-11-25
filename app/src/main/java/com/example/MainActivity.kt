package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.ui.AppNavigation
import com.example.ui.theme.ModestTailorTheme
import com.example.util.configureAppCheck

class MainActivity : ComponentActivity() {
    private var incomingDeepLinkUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure Firebase App Check with launch intent before any UI, ViewModels, or Firebase calls
        configureAppCheck(this, intent)
        incomingDeepLinkUri = intent?.data

        // STEP 2: Enable Edge-to-Edge rendering and launch Compose UI
        enableEdgeToEdge()
        setContent {
            ModestTailorTheme {
                AppNavigation(
                    incomingDeepLinkUri = incomingDeepLinkUri,
                    onConsumeDeepLink = { incomingDeepLinkUri = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        incomingDeepLinkUri = intent.data
    }
}

