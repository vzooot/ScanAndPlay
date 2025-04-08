package com.example.scanandplay.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.scanandplay.R
import com.example.scanandplay.ui.components.LeaderboardSection
import com.example.scanandplay.ui.dialogs.AdminPasswordPrompt
import com.example.scanandplay.viewmodel.ContentViewModel

@Composable
fun ContentScreen(viewModel: ContentViewModel, navController: NavHostController) {
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

            Text(
                "SVEA Spin Masters",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            LeaderboardSection(viewModel.leaderboard)

            Text("Start New Tournament", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { viewModel.selectPlayerCount(8, navController) }) {
                    Text("8 Players")
                }
                Button(onClick = { viewModel.selectPlayerCount(16, navController) }) {
                    Text("16 Players")
                }
            }
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
}
