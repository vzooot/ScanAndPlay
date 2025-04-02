package com.example.scanandplay.model

import java.util.*

data class Match(
    val id: UUID = UUID.randomUUID(),
    val round: Int,
    val number: Int,
    val bracket: String, // "W", "L", or "G"
    var opponent1: Participant? = null,
    var opponent2: Participant? = null,
    var winner: Participant? = null,
    var nextMatchForWinner: UUID? = null,
    var nextMatchForLoser: UUID? = null
)
