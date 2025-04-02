package com.example.scanandplay.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scanandplay.model.Match
import com.example.scanandplay.model.Stage
import com.example.scanandplay.ui.components.MatchView

@Composable
fun BracketHistoryView(stage: Stage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(stage.name, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
            BracketSection(title = "🏆 Winner", matches = stage.matches.filter { it.bracket == "W" })
            BracketSection(title = "💀 Loser", matches = stage.matches.filter { it.bracket == "L" })

            stage.matches.firstOrNull { it.bracket == "G" }?.let { gf ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("👑 Grand Final", style = MaterialTheme.typography.titleMedium)
                    MatchView(match = gf, onSelectWinner = {}, modifier = Modifier) // disabled
                }
            }
        }
    }
}

@Composable
private fun BracketSection(title: String, matches: List<Match>) {
    val grouped = matches.groupBy { it.round }.toSortedMap()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            grouped.forEach { (round, roundMatches) ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("R$round", style = MaterialTheme.typography.labelSmall)
                    roundMatches.forEach { match ->
                        MatchView(match = match, onSelectWinner = {}, modifier = Modifier)
                    }
                }
            }
        }
    }
}
