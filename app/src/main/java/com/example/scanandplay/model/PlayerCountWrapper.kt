package com.example.scanandplay.model

import java.util.*

data class PlayerCountWrapper(
    val count: Int,
    val id: UUID = UUID.randomUUID()
)
