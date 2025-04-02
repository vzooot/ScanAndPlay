package com.example.scanandplay.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.scanandplay.model.Stage
import java.io.File
import java.util.*

class TournamentHistoryManager private constructor(private val context: Context) {

    private val gson = Gson()
    private val fileName = "ScanAndPlay_Tournaments.json"

    private val saved: MutableList<Stage> = mutableListOf()

    val savedTournaments: List<Stage>
        get() = saved

    fun saveTournament(stage: Stage) {
        val numbered = stage.copy(name = "Tournament #${saved.size + 1}")
        saved.add(numbered)
        persist()
    }

    fun clear() {
        saved.clear()
        getFile().delete()
    }

    private fun persist() {
        try {
            val data = gson.toJson(saved)
            getFile().writeText(data)
        } catch (e: Exception) {
            println("❌ Failed to save tournaments: ${e.message}")
        }
    }

    private fun load() {
        try {
            val raw = getFile().readText()
            val type = object : TypeToken<List<Stage>>() {}.type
            val list: List<Stage> = gson.fromJson(raw, type)
            saved.clear()
            saved.addAll(list)
        } catch (e: Exception) {
            println("❌ Failed to load tournaments: ${e.message}")
        }
    }

    private fun getFile(): File {
        return File(context.filesDir, fileName)
    }

    companion object {
        private var instance: TournamentHistoryManager? = null

        fun initialize(context: Context) {
            if (instance == null) {
                instance = TournamentHistoryManager(context).apply { load() }
            }
        }

        fun get(): TournamentHistoryManager {
            return instance ?: throw IllegalStateException("TournamentHistoryManager not initialized")
        }
    }
}
