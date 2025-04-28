package com.example.scanandplay.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.scanandplay.ui.screens.*
import com.example.scanandplay.viewmodel.ContentViewModel
import com.example.scanandplay.navigation.Routes
import com.example.scanandplay.repository.LeaderboardManager

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: ContentViewModel
) {
    NavHost(navController = navController, startDestination = Routes.Home) {
        composable(Routes.Home) {
            ContentScreen(viewModel, navController)
        }

        composable("player_selection/{playerCount}") { backStackEntry ->
            val count = backStackEntry.arguments?.getString("playerCount")?.toIntOrNull() ?: 8
            PlayerSelectionScreen(
                playerLimit = count,
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("bracket") {
            BracketScreen(
                manager = viewModel.bracketsManager,
                leaderboard = LeaderboardManager.instance,
                viewModel = viewModel,
                onClose = {
                    // maybe popBackStack
                }
            )
        }


        composable(Routes.History) {
            TournamentHistoryScreen()
        }
    }
}
