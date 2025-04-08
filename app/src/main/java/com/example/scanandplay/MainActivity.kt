package com.example.scanandplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.scanandplay.repository.PlayerRegistry
import com.example.scanandplay.repository.TournamentHistoryManager
import com.example.scanandplay.ui.navigation.NavGraph
import com.example.scanandplay.ui.screens.ContentScreen
import com.example.scanandplay.ui.theme.ScanAndPlayTheme
import com.example.scanandplay.viewmodel.ContentViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TournamentHistoryManager.initialize(applicationContext)
        PlayerRegistry.initialize(applicationContext) // ✅ Add this line

        setContent {
            ScanAndPlayTheme {
                val viewModel: ContentViewModel = viewModel()
                val navController = rememberNavController()

                NavGraph(navController = navController, viewModel = viewModel)
            }
        }
    }
}
