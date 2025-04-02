package com.example.scanandplay.model

import java.util.*

data class LeaderboardEntry(
    val id: UUID,
    val name: String,
    var points: Int
)
