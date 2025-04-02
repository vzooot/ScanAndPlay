package com.example.scanandplay.logic

import com.example.scanandplay.model.*
import com.example.scanandplay.repository.LeaderboardManager
import com.example.scanandplay.repository.TournamentHistoryManager
import java.util.*

class BracketsManager {

    var stage: Stage? = null
        private set

    fun resetTournament() {
        stage = null
    }

    fun create8PlayerDoubleElimination(name: String, participants: List<Participant>) {
        val matches = mutableListOf<Match>()

        // Winner Round 1
        val w1 = List(4) { i ->
            Match(
                round = 1, number = i + 1, bracket = "W",
                opponent1 = participants[i * 2],
                opponent2 = participants[i * 2 + 1]
            )
        }

        // Winner Round 2
        val w2 = List(2) { i ->
            Match(round = 2, number = i + 1, bracket = "W")
        }

        // Winner Final
        val wFinal = Match(round = 3, number = 1, bracket = "W")

        // Loser Rounds
        val l1 = List(2) { Match(round = 1, number = it + 1, bracket = "L") }
        val l2 = List(2) { Match(round = 2, number = it + 1, bracket = "L") }
        val l3 = Match(round = 3, number = 1, bracket = "L")

        val gf = Match(round = 1, number = 1, bracket = "G")

        // Winner routing
        w1[0].nextMatchForWinner = w2[0].id
        w1[1].nextMatchForWinner = w2[0].id
        w1[2].nextMatchForWinner = w2[1].id
        w1[3].nextMatchForWinner = w2[1].id

        w2[0].nextMatchForWinner = wFinal.id
        w2[1].nextMatchForWinner = wFinal.id

        wFinal.nextMatchForWinner = gf.id

        // Loser routing
        w1[0].nextMatchForLoser = l1[0].id
        w1[1].nextMatchForLoser = l1[0].id
        w1[2].nextMatchForLoser = l1[1].id
        w1[3].nextMatchForLoser = l1[1].id

        w2[0].nextMatchForLoser = l2[0].id
        w2[1].nextMatchForLoser = l2[1].id

        l1[0].nextMatchForWinner = l2[0].id
        l1[1].nextMatchForWinner = l2[1].id

        l2[0].nextMatchForWinner = l3.id
        l2[1].nextMatchForWinner = l3.id

        l3.nextMatchForWinner = gf.id

        matches.addAll(w1 + w2 + listOf(wFinal))
        matches.addAll(l1 + l2 + listOf(l3))
        matches.add(gf)

        stage = Stage(id = UUID.randomUUID(), name = name, type = StageType.DoubleElimination, matches = matches.toMutableList())
    }

    fun reportMatchResult(matchId: UUID, winner: Participant) {
        val currentStage = stage ?: return
        val matchIndex = currentStage.matches.indexOfFirst { it.id == matchId }
        if (matchIndex == -1) return

        val match = currentStage.matches[matchIndex]
        if (match.winner != null || match.opponent1 == null || match.opponent2 == null) return

        val loser = if (winner == match.opponent1) match.opponent2 else match.opponent1
        match.winner = winner
        currentStage.matches[matchIndex] = match

        fun route(toId: UUID?, participant: Participant?) {
            val nextIndex = currentStage.matches.indexOfFirst { it.id == toId }
            if (nextIndex != -1 && participant != null) {
                val next = currentStage.matches[nextIndex]
                if (next.opponent1 == null) {
                    next.opponent1 = participant
                } else {
                    next.opponent2 = participant
                }
                currentStage.matches[nextIndex] = next
            }
        }

        route(match.nextMatchForWinner, winner)
        route(match.nextMatchForLoser, loser)

        stage = currentStage
    }

    fun finalizeTournament(leaderboard: LeaderboardManager) {
        val currentStage = stage ?: return
        val allParticipants = currentStage.matches.flatMap { listOfNotNull(it.opponent1, it.opponent2) }.toSet()

        val points = PointSystem()
        allParticipants.forEach {
            leaderboard.addPoints(it, points.participation)
        }

        currentStage.matches.forEach { match ->
            match.winner?.let {
                leaderboard.addPoints(it, points.win)
            }
        }

        currentStage.matches.find { it.bracket == "G" }?.let { grandFinal ->
            grandFinal.winner?.let {
                leaderboard.addPoints(it, points.first)
            }

            val finalist = if (grandFinal.opponent1 == grandFinal.winner) grandFinal.opponent2 else grandFinal.opponent1
            finalist?.let { leaderboard.addPoints(it, points.second) }
        }

        currentStage.matches.filter { it.bracket == "L" }.lastOrNull()?.let { loserFinal ->
            loserFinal.winner?.let { leaderboard.addPoints(it, points.third) }
            val fourth = if (loserFinal.opponent1 == loserFinal.winner) loserFinal.opponent2 else loserFinal.opponent1
            fourth?.let { leaderboard.addPoints(it, points.fourth) }
        }

        TournamentHistoryManager.get().saveTournament(currentStage)
    }

    fun addGrandFinalIfNeeded() {
        val currentStage = stage ?: return
        if (currentStage.matches.any { it.bracket == "G" }) return

        val winnerFinal = currentStage.matches.lastOrNull { it.bracket == "W" && it.winner != null } ?: return
        val loserFinal = currentStage.matches.lastOrNull { it.bracket == "L" && it.winner != null } ?: return

        val grandFinal = Match(
            round = 1,
            number = 1,
            bracket = "G",
            opponent1 = winnerFinal.winner,
            opponent2 = loserFinal.winner
        )
        currentStage.matches.add(grandFinal)
        stage = currentStage
    }
}
