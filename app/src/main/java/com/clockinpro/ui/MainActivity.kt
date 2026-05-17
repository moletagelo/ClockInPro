package com.clockinpro.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.clockinpro.ui.navigation.AppNavigation
import com.clockinpro.ui.theme.ClockInProTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClockInProTheme {
                val viewModel: MainViewModel = hiltViewModel()
                val isLoggedIn by viewModel.isLoggedIn.collectAsState(initial = false)

                AppNavigation(isLoggedIn = isLoggedIn)
            }
        }
    }
}
