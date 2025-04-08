package com.example.scanandplay.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scanandplay.model.Match
import com.example.scanandplay.model.Participant

@Composable
fun MatchView(
    match: Match,
    onSelectWinner: (Participant) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 4.dp,
        modifier = modifier
            .width(120.dp)
            .padding(4.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(4.dp)
        ) {
            MatchButton(
                match = match,
                participant = match.opponent1,
                onSelect = onSelectWinner
            )
            MatchButton(
                match = match,
                participant = match.opponent2,
                onSelect = onSelectWinner
            )
        }
    }
}


@Composable
private fun MatchButton(
    match: Match,
    participant: Participant?,
    onSelect: (Participant) -> Unit
) {
    val matchHasWinner = match.winner != null
    val isParticipantWinner = match.winner == participant
    val isActiveMatch = !matchHasWinner &&
            match.opponent1 != null &&
            match.opponent2 != null

    val isClickable = participant != null && isActiveMatch

    val backgroundColor = when {
        isParticipantWinner -> Color(0xFFA5D6A7) // ✅ Green
        else -> Color(0xFFE0E0E0) // ⬜ Gray for all others
    }

    val textColor = when {
        participant == null -> Color.Black // TBD
        isActiveMatch -> Color(0xFF1976D2) // 🔵 Blue for active
        else -> Color.Black // Locked = black
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .clickable(enabled = isClickable) {
                participant?.let(onSelect)
            }
    ) {
        Text(
            text = participant?.name ?: "TBD",
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}



