package com.example.scanandplay.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scanandplay.model.LeaderboardEntry

@Composable
fun LeaderboardSection(entries: List<LeaderboardEntry>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF7F7F7))
            .padding(12.dp)
            .defaultMinSize(minHeight = 50.dp)
    ) {
        Text("Leaderboard", style = MaterialTheme.typography.titleMedium)

        if (entries.isEmpty()) {
            Text("No players ranked yet.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            entries.forEach {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("🏓 ${it.name}", style = MaterialTheme.typography.bodySmall)
                    Text("${it.points} pts", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                }
            }
        }
    }
}
