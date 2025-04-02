package com.example.scanandplay.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.scanandplay.R
import com.example.scanandplay.model.PlayerCountWrapper
import com.example.scanandplay.ui.components.LeaderboardSection
import com.example.scanandplay.ui.dialogs.AdminPasswordPrompt
import com.example.scanandplay.ui.navigation.NavGraph
import com.example.scanandplay.viewmodel.ContentViewModel

@Composable
fun ContentScreen() {
    val viewModel: ContentViewModel = viewModel()
    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.melo),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Text("SVEA Spin Masters", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            LeaderboardSection(viewModel.leaderboard)

            Text("Start New Tournament", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { viewModel.selectPlayerCount(8, navController) }) {
                    Text("8 Players")
                }
                Button(onClick = { viewModel.selectPlayerCount(16, navController) }) {
                    Text("16 Players")
                }
                Button(onClick = { viewModel.requestAdminAction(ContentViewModel.AdminAction.ResetPoints) }) {
                    Text("Reset Points")
                }
            }

            Button(
                onClick = { viewModel.showHistory(navController) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text("Tournament History")
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        if (viewModel.isPasswordPromptVisible) {
            AdminPasswordPrompt(
                password = viewModel.adminPassword,
                onPasswordChange = { viewModel.adminPassword = it },
                onCancel = { viewModel.dismissAdminPrompt() },
                onConfirm = { viewModel.handleAdminAction() },
                shakeTrigger = viewModel.shakeTrigger
            )
        }
    }

    NavGraph(navController = navController, viewModel = viewModel)
}
