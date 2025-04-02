package com.example.scanandplay.repository

import android.content.Context
import com.example.scanandplay.model.LeaderboardEntry
import com.example.scanandplay.model.Participant
import com.example.scanandplay.model.PointSystem
import java.util.*

class LeaderboardManager private constructor() {

    private val _scores: MutableMap<UUID, Int> = mutableMapOf()
    private val _entries: MutableList<LeaderboardEntry> = mutableListOf()

    val leaderboard: List<LeaderboardEntry>
        get() = _entries.sortedByDescending { it.points }

    fun addPoints(player: Participant, points: Int) {
        val current = _scores.getOrDefault(player.id, 0)
        val newScore = current + points
        _scores[player.id] = newScore

        val existing = _entries.indexOfFirst { it.id == player.id }
        if (existing >= 0) {
            _entries[existing].points = newScore
        } else {
            _entries.add(LeaderboardEntry(id = player.id, name = player.name, points = newScore))
        }
    }

    fun reset() {
        _scores.clear()
        _entries.clear()
    }

    fun addResults(
        players: List<Participant>,
        placements: Map<UUID, Int>,
        matchWins: Map<UUID, Int>
    ) {
        val system = PointSystem()

        for (player in players) {
            var total = system.participation

            val wins = matchWins[player.id] ?: 0
            total += wins * system.win

            when (placements[player.id]) {
                1 -> total += system.first
                2 -> total += system.second
                3 -> total += system.third
                4 -> total += system.fourth
            }

            addPoints(player, total)
        }
    }

    companion object {
        val instance = LeaderboardManager()
    }
}
