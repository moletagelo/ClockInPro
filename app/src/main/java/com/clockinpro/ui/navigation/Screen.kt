package com.clockinpro.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Settings : Screen("settings")
    object TargetDetail : Screen("target/{targetId}") {
        fun createRoute(targetId: Long): String = "target/$targetId"
    }
}
