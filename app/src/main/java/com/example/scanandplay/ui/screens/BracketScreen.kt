package com.example.scanandplay.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.scanandplay.logic.BracketsManager
import com.example.scanandplay.model.Match
import com.example.scanandplay.model.Participant
import com.example.scanandplay.repository.LeaderboardManager
import com.example.scanandplay.ui.components.MatchView
import com.example.scanandplay.viewmodel.ContentViewModel

@Composable
fun BracketScreen(
    manager: BracketsManager,
    leaderboard: LeaderboardManager,
    viewModel: ContentViewModel, // 👈 add this
    onClose: () -> Unit
) {


    var showWinnerPopup by remember { mutableStateOf(false) }
    var tournamentWinner by remember { mutableStateOf<Participant?>(null) }
    var selectedMatch by remember { mutableStateOf<Match?>(null) }
    var selectedWinner by remember { mutableStateOf<Participant?>(null) }
    var confirmPopupVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                Text("🏆 Winner Bracket", style = MaterialTheme.typography.titleSmall)
                BracketRounds(
                    matches = manager.stage?.matches?.filter { it.bracket == "W" } ?: emptyList()
                ) { match, winner ->
                    selectedMatch = match
                    selectedWinner = winner
                    confirmPopupVisible = true
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = com.example.scanandplay.R.drawable.melo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = "Andra chansen",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                BracketRounds(
                    matches = manager.stage?.matches?.filter { it.bracket == "L" } ?: emptyList()
                ) { match, winner ->
                    selectedMatch = match
                    selectedWinner = winner
                    confirmPopupVisible = true
                }

                Spacer(modifier = Modifier.height(24.dp))

                manager.stage?.matches?.firstOrNull { it.bracket == "G" }?.let { grandFinal ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👑 Grand Final", style = MaterialTheme.typography.titleMedium)
                        MatchView(match = grandFinal, onSelectWinner = {
                            selectedMatch = grandFinal
                            selectedWinner = it
                            confirmPopupVisible = true
                        })
                    }
                }
            }
        }
    }


    if (showWinnerPopup && tournamentWinner != null) {
        WinnerPopup(
            winner = tournamentWinner!!,
            onClose = {
                manager.finalizeTournament(leaderboard)
                viewModel.refreshLeaderboard() // 👈 use the injected viewModel here
                onClose()
            }
        )
    }

    if (confirmPopupVisible && selectedMatch != null && selectedWinner != null) {
        ConfirmWinnerDialog(
            participant = selectedWinner!!,
            onConfirm = {
                manager.reportMatchResult(selectedMatch!!.id, selectedWinner!!)
                if (selectedMatch!!.bracket == "G") {
                    tournamentWinner = selectedWinner
                    showWinnerPopup = true
                }
                confirmPopupVisible = false
            },
            onCancel = {
                confirmPopupVisible = false
            }
        )
    }
}

@Composable
private fun BracketRounds(
    matches: List<Match>,
    onWinnerSelect: (Match, Participant) -> Unit
) {
    val grouped = matches.groupBy { it.round }.toSortedMap()

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        grouped.forEach { (round, roundMatches) ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("R$round", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                roundMatches.forEach { match ->
                    MatchView(
                        match = match,
                        onSelectWinner = { winner ->
                            onWinnerSelect(match, winner)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfirmWinnerDialog(
    participant: Participant,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Confirm Winner") },
        text = { Text("Are you sure you want to select ${participant.name} as winner?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun WinnerPopup(
    winner: Participant,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(Color.White, MaterialTheme.shapes.medium)
                .padding(24.dp)
                .width(260.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("🏆 Winner", style = MaterialTheme.typography.titleMedium, color = Color(0xFFFFC107))
            Text(winner.name, style = MaterialTheme.typography.headlineSmall)
            Text("is the new Spin Master! 👑")

            Button(onClick = onClose) {
                Text("Close")
            }
        }
    }
}
