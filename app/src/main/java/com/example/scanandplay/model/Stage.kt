package com.example.scanandplay.model

import java.util.*

data class Stage(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    val type: StageType,
    val matches: MutableList<Match>,
    val createdAt: Date = Date()
)
