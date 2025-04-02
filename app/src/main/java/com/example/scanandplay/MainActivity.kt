package com.example.scanandplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.scanandplay.ui.screens.ContentScreen
import com.example.scanandplay.ui.theme.ScanAndPlayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScanAndPlayTheme {
                ContentScreen()
            }
        }
    }
}
