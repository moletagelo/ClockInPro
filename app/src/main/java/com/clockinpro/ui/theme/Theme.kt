package com.clockinpro.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDark,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = Secondary.copy(alpha = 0.2f),
    onSecondaryContainer = SecondaryDark,
    tertiary = Accent,
    onTertiary = OnPrimary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = Background,
    onSurfaceVariant = TextSecondary,
    error = Error,
    onError = OnError,
    outline = Divider
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDarkTheme,
    onPrimary = PrimaryDark,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = PrimaryLight,
    secondary = SecondaryDarkTheme,
    onSecondary = SecondaryDark,
    secondaryContainer = SecondaryDark,
    onSecondaryContainer = SecondaryDarkTheme,
    tertiary = Accent,
    onTertiary = OnPrimary,
    background = BackgroundDarkTheme,
    onBackground = OnBackgroundDarkTheme,
    surface = SurfaceDarkTheme,
    onSurface = OnSurfaceDarkTheme,
    surfaceVariant = SurfaceDarkTheme,
    onSurfaceVariant = OnSurfaceDarkTheme.copy(alpha = 0.7f),
    error = Error,
    onError = OnError,
    outline = Divider.copy(alpha = 0.3f)
)

@Composable
fun ClockInProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
