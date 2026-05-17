package com.clockinpro.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Main : Screen("main")
    object Home : Screen("home")
    object CheckIn : Screen("checkin")
    object Record : Screen("record")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object Reminder : Screen("reminder")
}
