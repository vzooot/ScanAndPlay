package com.example.scanandplay.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.scanandplay.model.PlayerCountWrapper
import com.example.scanandplay.ui.screens.*
import com.example.scanandplay.viewmodel.ContentViewModel
import com.example.scanandplay.navigation.Routes

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: ContentViewModel
) {
    NavHost(navController = navController, startDestination = Routes.Home + "/{playerCount}") {
        composable("${Routes.Home}/{playerCount}") { backStackEntry ->
            val count = backStackEntry.arguments?.getString("playerCount")?.toIntOrNull() ?: 8
            HomeScreen(
                playerLimit = count,
                navController = navController,
                leaderboard = viewModel.leaderboard
            )
        }
        composable(Routes.History) {
            TournamentHistoryScreen()
        }
    }
}
