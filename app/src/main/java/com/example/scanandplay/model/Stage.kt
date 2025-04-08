package com.example.scanandplay.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.util.*

data class Stage(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    val type: StageType,
    val matches: SnapshotStateList<Match> = mutableStateListOf(),
    val createdAt: Date = Date()
)
