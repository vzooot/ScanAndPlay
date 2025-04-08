package com.example.scanandplay.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.scanandplay.logic.BracketsManager
import com.example.scanandplay.model.Participant
import com.example.scanandplay.repository.LeaderboardManager
import com.example.scanandplay.repository.PlayerRegistry
import com.example.scanandplay.viewmodel.ContentViewModel

@Composable
fun PlayerSelectionScreen(
    playerLimit: Int,
    navController: NavHostController,
    viewModel: ContentViewModel // ✅ Add this
) {
    val registry = PlayerRegistry.instance
    val manager = remember { BracketsManager() }

    var selectedPlayers by remember { mutableStateOf<List<Participant>>(emptyList()) }
    var newPlayerName by remember { mutableStateOf("") }
    var showBracket by remember { mutableStateOf(false) }

    if (showBracket) {
        BracketScreen(
            manager = manager,
            leaderboard = LeaderboardManager.instance,
            viewModel = viewModel, // ✅ Pass it
            onClose = {
                showBracket = false
                navController.popBackStack()
            }
        )
        return
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // LEFT: Selected Players + Start Button
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("New Tournament", style = MaterialTheme.typography.titleLarge)
            Text("Select up to $playerLimit players", color = Color.Gray)

            Text(
                "Selected Players (${selectedPlayers.size}/$playerLimit)",
                style = MaterialTheme.typography.titleSmall
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(selectedPlayers) { player ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(player.name)
                        TextButton(onClick = {
                            selectedPlayers = selectedPlayers - player
                        }) {
                            Text("Remove", color = Color.Red)
                        }
                    }
                }
            }

            Divider()

            Button(
                onClick = {
                    val ordered = selectedPlayers.take(playerLimit)
                    if (playerLimit == 8) {
                        manager.create8PlayerDoubleElimination("Tournament", ordered)
                    } else if (playerLimit == 16) {
                        manager.create16PlayerDoubleElimination("Tournament", ordered)
                    }
                    showBracket = true
                },
                enabled = selectedPlayers.size == playerLimit
            ) {
                Text("🎯 Start Tournament")
            }


//            Button(
//                onClick = {
//                    val shuffled = selectedPlayers.shuffled().take(playerLimit)
//                    if (shuffled.size == 8) {
//                        manager.create8PlayerDoubleElimination("Tournament", shuffled)
//                    } else if (shuffled.size == 16) {
//                        manager.create16PlayerDoubleElimination("Tournament", shuffled)
//                    }
//                    showBracket = true
//                },
//                enabled = selectedPlayers.size == playerLimit
//            ) {
//                Text("🎯 Start Tournament")
//            }
        }

        // RIGHT: All Registered Players + Add Field
        Column(modifier = Modifier.width(280.dp)) {
            Text("All Registered Players", style = MaterialTheme.typography.titleSmall)

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newPlayerName,
                    onValueChange = { newPlayerName = it },
                    label = { Text("Enter name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val trimmed = newPlayerName.trim()
                            if (trimmed.isNotEmpty()) {
                                registry.register(trimmed)
                                newPlayerName = ""
                            }
                        }
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    val trimmed = newPlayerName.trim()
                    if (trimmed.isNotEmpty()) {
                        registry.register(trimmed)
                        newPlayerName = ""
                    }
                }) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF9F9F9))
                    .padding(8.dp)
            ) {
                items(registry.registeredPlayers) { player ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = {
                            if (selectedPlayers.size < playerLimit && !selectedPlayers.contains(player)) {
                                selectedPlayers = selectedPlayers + player
                            }
                        }) {
                            Text(player.name)
                        }

                        IconButton(onClick = {
                            registry.unregister(player)
                            selectedPlayers = selectedPlayers - player
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}
