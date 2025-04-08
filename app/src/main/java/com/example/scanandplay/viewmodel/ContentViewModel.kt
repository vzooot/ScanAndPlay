package com.example.scanandplay.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.scanandplay.model.LeaderboardEntry
import com.example.scanandplay.model.PlayerCountWrapper
import com.example.scanandplay.navigation.Routes
import com.example.scanandplay.repository.LeaderboardManager

class ContentViewModel : ViewModel() {

    enum class AdminAction {
        ResetPoints, Export, Clear
    }

    var leaderboard by mutableStateOf(LeaderboardManager.instance.leaderboard)
        private set

    var adminPassword by mutableStateOf("")
    var isPasswordPromptVisible by mutableStateOf(false)
    var pendingAction: AdminAction? = null
    var shakeTrigger by mutableStateOf(0f)

    fun refreshLeaderboard() {
        leaderboard = LeaderboardManager.instance.leaderboard
    }

    fun selectPlayerCount(count: Int, navController: NavHostController) {
        navController.navigate("${Routes.PlayerSelection}/$count")
    }

    fun showHistory(navController: NavHostController) {
        navController.navigate(Routes.History)
    }

    fun requestAdminAction(action: AdminAction) {
        pendingAction = action
        isPasswordPromptVisible = true
    }

    fun dismissAdminPrompt() {
        adminPassword = ""
        isPasswordPromptVisible = false
        pendingAction = null
    }

    fun handleAdminAction() {
        if (adminPassword != AdminMode.password) {
            shakeTrigger += 1f
            return
        }

        when (pendingAction) {
            AdminAction.ResetPoints -> LeaderboardManager.instance.reset()
            AdminAction.Clear -> {} // Future use
            AdminAction.Export -> {} // Future use
            null -> {}
        }

        leaderboard = LeaderboardManager.instance.leaderboard
        dismissAdminPrompt()
    }

    private object AdminMode {
        const val password = "1234"
    }
}
