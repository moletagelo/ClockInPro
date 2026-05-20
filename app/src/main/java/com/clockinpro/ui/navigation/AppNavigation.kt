package com.clockinpro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.clockinpro.ui.MainUiState
import com.clockinpro.v2.ui.detail.TargetDetailRoute
import com.clockinpro.v2.ui.home.HomeRoute
import com.clockinpro.v2.ui.onboarding.OnboardingRoute
import com.clockinpro.v2.ui.settings.SettingsRoute

@Composable
fun AppNavigation(
    mainUiState: MainUiState,
    navController: NavHostController = rememberNavController()
) {
    val startDestination = if (mainUiState.hasCompletedOnboarding) {
        Screen.Home.route
    } else {
        Screen.Onboarding.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingRoute(
                onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeRoute(
                onOpenSettings = { navController.navigate(Screen.Settings.route) },
                onOpenTarget = { targetId ->
                    navController.navigate(Screen.TargetDetail.createRoute(targetId))
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsRoute(onBack = { navController.popBackStack() })
        }
        composable(
            route = Screen.TargetDetail.route,
            arguments = listOf(navArgument("targetId") { type = NavType.LongType })
        ) {
            TargetDetailRoute(onBack = { navController.popBackStack() })
        }
    }
}
