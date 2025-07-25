package com.example.scanandplay.logic

import androidx.compose.runtime.mutableStateListOf
import com.example.scanandplay.model.*
import com.example.scanandplay.repository.LeaderboardManager
import com.example.scanandplay.repository.TournamentHistoryManager
import java.util.*

import androidx.lifecycle.ViewModel

class BracketsManager : ViewModel() {

    var stage: Stage? = null
        private set

    fun create8PlayerDoubleElimination(name: String, participants: List<Participant>) {
        if (participants.size != 8) {
            println("❌ Exactly 8 participants required.")
            return
        }

        val matches = mutableListOf<Match>()

        val w1 = List(4) { i ->
            Match(round = 1, number = i + 1, bracket = "W", opponent1 = participants[i * 2], opponent2 = participants[i * 2 + 1])
        }
        val w2 = List(2) { i -> Match(round = 2, number = i + 1, bracket = "W") }
        val wFinal = Match(round = 3, number = 1, bracket = "W")

        val l1 = List(2) { Match(round = 1, number = it + 1, bracket = "L") }
        val l2 = List(2) { Match(round = 2, number = it + 1, bracket = "L") }
        val l3 = Match(round = 3, number = 1, bracket = "L")
        val lFinal = Match(round = 4, number = 1, bracket = "L")

        val gf = Match(round = 1, number = 1, bracket = "G")

        // Winner routes
        w1[0].nextMatchForWinner = w2[0].id
        w1[1].nextMatchForWinner = w2[0].id
        w1[2].nextMatchForWinner = w2[1].id
        w1[3].nextMatchForWinner = w2[1].id
        w2[0].nextMatchForWinner = wFinal.id
        w2[1].nextMatchForWinner = wFinal.id
        wFinal.nextMatchForWinner = gf.id

        // Loser routes
        w1[0].nextMatchForLoser = l1[0].id
        w1[1].nextMatchForLoser = l1[0].id
        w1[2].nextMatchForLoser = l1[1].id
        w1[3].nextMatchForLoser = l1[1].id
        l1[0].nextMatchForWinner = l2[0].id
        l1[1].nextMatchForWinner = l2[1].id
        w2[0].nextMatchForLoser = l2[0].id
        w2[1].nextMatchForLoser = l2[1].id
        l2[0].nextMatchForWinner = l3.id
        l2[1].nextMatchForWinner = l3.id
        l3.nextMatchForWinner = lFinal.id
        wFinal.nextMatchForLoser = lFinal.id
        lFinal.nextMatchForWinner = gf.id

        matches.addAll(w1 + w2 + listOf(wFinal))
        matches.addAll(l1 + l2 + listOf(l3, lFinal))
        matches.add(gf)

        stage = Stage(
            name = name,
            type = StageType.DoubleElimination,
            matches = mutableStateListOf<Match>().apply { addAll(matches) }
        )
    }

    fun create16PlayerDoubleElimination(name: String, participants: List<Participant>) {
        if (participants.size != 16) {
            println("❌ Exactly 16 participants required.")
            return
        }

        val matches = mutableListOf<Match>()

        val w1 = List(8) { i -> Match(round = 1, number = i + 1, bracket = "W", opponent1 = participants[i * 2], opponent2 = participants[i * 2 + 1]) }
        val w2 = List(4) { i -> Match(round = 2, number = i + 1, bracket = "W") }
        val w3 = List(2) { i -> Match(round = 3, number = i + 1, bracket = "W") }
        val wFinal = Match(round = 4, number = 1, bracket = "W")

        val l1 = List(4) { i -> Match(round = 1, number = i + 1, bracket = "L") }
        val l2 = List(4) { i -> Match(round = 2, number = i + 1, bracket = "L") }
        val l3 = List(2) { i -> Match(round = 3, number = i + 1, bracket = "L") }
        val l4 = List(2) { i -> Match(round = 4, number = i + 1, bracket = "L") }
        val l5 = Match(round = 5, number = 1, bracket = "L")
        val lFinal = Match(round = 6, number = 1, bracket = "L")

        val gf = Match(round = 1, number = 1, bracket = "G")

        // Winner progression
        w1.forEachIndexed { i, match ->
            match.nextMatchForWinner = w2[i / 2].id
            match.nextMatchForLoser = l1[i / 2].id
        }
        w2.forEachIndexed { i, match ->
            match.nextMatchForWinner = w3[i / 2].id
            match.nextMatchForLoser = l2[i].id
        }
        w3.forEachIndexed { i, match ->
            match.nextMatchForWinner = wFinal.id
            match.nextMatchForLoser = l4[i].id
        }
        wFinal.nextMatchForWinner = gf.id
        wFinal.nextMatchForLoser = lFinal.id

        // Loser progression
        l1.forEachIndexed { i, match -> match.nextMatchForWinner = l2[i].id }
        l2.forEachIndexed { i, match -> match.nextMatchForWinner = l3[i / 2].id }
        l3.forEachIndexed { i, match -> match.nextMatchForWinner = l4[i].id }
        l4.forEachIndexed { i, match -> match.nextMatchForWinner = l5.id }
        l5.nextMatchForWinner = lFinal.id
        lFinal.nextMatchForWinner = gf.id

        matches.addAll(w1 + w2 + w3 + listOf(wFinal))
        matches.addAll(l1 + l2 + l3 + l4 + listOf(l5, lFinal))
        matches.add(gf)

        stage = Stage(
            name = name,
            type = StageType.DoubleElimination,
            matches = mutableStateListOf<Match>().apply { addAll(matches) }
        )
    }

    fun reportMatchResult(matchId: UUID, winner: Participant) {
        val currentStage = stage ?: return
        val matchIndex = currentStage.matches.indexOfFirst { it.id == matchId }
        if (matchIndex == -1) return

        val match = currentStage.matches[matchIndex]
        if (match.winner != null || match.opponent1 == null || match.opponent2 == null) return

        val loser = if (winner == match.opponent1) match.opponent2 else match.opponent1

        val updatedMatch = match.copy(winner = winner)
        currentStage.matches[matchIndex] = updatedMatch

        fun route(toId: UUID?, participant: Participant?) {
            val nextIndex = currentStage.matches.indexOfFirst { it.id == toId }
            if (nextIndex != -1 && participant != null) {
                val next = currentStage.matches[nextIndex]
                val updated = when {
                    next.opponent1 == null -> next.copy(opponent1 = participant)
                    next.opponent2 == null -> next.copy(opponent2 = participant)
                    else -> next
                }
                currentStage.matches[nextIndex] = updated
            }
        }

        route(updatedMatch.nextMatchForWinner, winner)
        route(updatedMatch.nextMatchForLoser, loser)
    }

    fun finalizeTournament(leaderboard: LeaderboardManager) {
        val currentStage = stage ?: return
        val points = PointSystem()

        val participants = currentStage.matches
            .flatMap { listOfNotNull(it.opponent1, it.opponent2) }
            .toSet()

        participants.forEach { leaderboard.addPoints(it, points.participation) }

        currentStage.matches.forEach { match ->
            match.winner?.let { leaderboard.addPoints(it, points.win) }
        }

        currentStage.matches.find { it.bracket == "G" }?.let { grandFinal ->
            grandFinal.winner?.let { leaderboard.addPoints(it, points.first) }
            val runnerUp = if (grandFinal.opponent1 == grandFinal.winner) grandFinal.opponent2 else grandFinal.opponent1
            runnerUp?.let { leaderboard.addPoints(it, points.second) }
        }

        currentStage.matches.filter { it.bracket == "L" }.lastOrNull()?.let { loserFinal ->
            loserFinal.winner?.let { leaderboard.addPoints(it, points.third) }
            val fourth = if (loserFinal.opponent1 == loserFinal.winner) loserFinal.opponent2 else loserFinal.opponent1
            fourth?.let { leaderboard.addPoints(it, points.fourth) }
        }

        // Quarterfinal losers (Winner Bracket Round 2 losers)
        val quarterFinalLosers = currentStage.matches
            .filter { it.bracket == "W" && it.round == 2 }
            .mapNotNull { match ->
                if (match.winner != null) {
                    if (match.opponent1 == match.winner) match.opponent2 else match.opponent1
                } else null
            }

        quarterFinalLosers.forEach { loser ->
            leaderboard.addPoints(loser, points.quarterFinal)
        }

        TournamentHistoryManager.get().saveTournament(currentStage)
    }
}
