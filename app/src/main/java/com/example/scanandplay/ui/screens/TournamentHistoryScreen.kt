package com.example.scanandplay.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scanandplay.repository.TournamentHistoryManager
import com.example.scanandplay.model.Stage
import com.example.scanandplay.ui.components.MatchView
import com.example.scanandplay.ui.dialogs.AdminPasswordDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentHistoryScreen() {
    val manager = TournamentHistoryManager.get()
    val tournaments = manager.savedTournaments

    var showPasswordPrompt by remember { mutableStateOf(false) }
    var adminPassword by remember { mutableStateOf("") }
    var pendingAction by remember { mutableStateOf<AdminAction?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Tournament History") })
        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = {
                    pendingAction = AdminAction.Export
                    showPasswordPrompt = true
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Export")
                    Spacer(Modifier.width(8.dp))
                    Text("Export")
                }
                OutlinedButton(onClick = {
                    pendingAction = AdminAction.Clear
                    showPasswordPrompt = true
                }, colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear")
                    Spacer(Modifier.width(8.dp))
                    Text("Clear")
                }
            }
        }
    ) { padding ->
        if (tournaments.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No tournaments saved yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(tournaments) { stage ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(stage.name, style = MaterialTheme.typography.titleMedium)
                        Text("Matches: ${stage.matches.size}", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        BracketHistoryView(stage = stage)
                    }
                }
            }
        }

        if (showPasswordPrompt) {
            AdminPasswordDialog(
                password = adminPassword,
                onPasswordChange = { adminPassword = it },
                onCancel = {
                    showPasswordPrompt = false
                    adminPassword = ""
                    pendingAction = null
                },
                onConfirm = {
                    if (adminPassword == AdminMode.password) {
                        when (pendingAction) {
                            AdminAction.Clear -> manager.clear()
                            AdminAction.Export -> {} // Optional
                            null -> {}
                        }
                    }
                    showPasswordPrompt = false
                    adminPassword = ""
                    pendingAction = null
                }
            )
        }
    }
}

private enum class AdminAction {
    Export, Clear
}

private object AdminMode {
    const val password = "1234"
}
