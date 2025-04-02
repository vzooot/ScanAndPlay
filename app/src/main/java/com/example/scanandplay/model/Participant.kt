package com.example.scanandplay.model

import java.util.*

data class Participant(
    val id: UUID = UUID.randomUUID(),
    val name: String
) {
    override fun toString(): String = name
}
