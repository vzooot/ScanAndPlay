package com.example.scanandplay.repository

import com.example.scanandplay.model.Participant
import java.util.*

class PlayerRegistry private constructor() {

    private val players: MutableList<Participant> = mutableListOf()

    val registeredPlayers: List<Participant>
        get() = players.sortedBy { it.name.lowercase(Locale.getDefault()) }

    fun register(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        if (players.any { it.name.equals(trimmed, ignoreCase = true) }) return

        players.add(Participant(name = trimmed))
    }

    fun unregister(player: Participant) {
        players.removeAll { it.id == player.id }
    }

    fun reset() {
        players.clear()
    }

    companion object {
        val instance = PlayerRegistry()
    }
}
