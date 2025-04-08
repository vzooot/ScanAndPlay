package com.example.scanandplay.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import com.example.scanandplay.model.Participant
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class PlayerRegistry private constructor(private val context: Context) {

    private val gson = Gson()
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ScanAndPlay_Prefs", Context.MODE_PRIVATE)
    private val key = "RegisteredPlayers"

    private val players = mutableStateListOf<Participant>()

    val registeredPlayers: List<Participant>
        get() = players.sortedBy { it.name.lowercase(Locale.getDefault()) }

    init {
        load()
    }

    fun register(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        if (players.any { it.name.equals(trimmed, ignoreCase = true) }) return

        players.add(Participant(name = trimmed))
        persist()
    }

    fun unregister(player: Participant) {
        players.removeAll { it.id == player.id }
        persist()
    }

    fun reset() {
        players.clear()
        persist()
    }

    private fun persist() {
        val json = gson.toJson(players)
        prefs.edit().putString(key, json).apply()
    }

    private fun load() {
        val json = prefs.getString(key, null) ?: return
        val type = object : TypeToken<List<Participant>>() {}.type
        val list: List<Participant> = gson.fromJson(json, type)
        players.clear()
        players.addAll(list)
    }

    companion object {
        private var _instance: PlayerRegistry? = null

        fun initialize(context: Context) {
            if (_instance == null) {
                _instance = PlayerRegistry(context)
            }
        }

        val instance: PlayerRegistry
            get() = requireNotNull(_instance) {
                "PlayerRegistry not initialized. Call initialize(context) first."
            }
    }
}
