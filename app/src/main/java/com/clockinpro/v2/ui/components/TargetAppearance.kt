package com.clockinpro.v2.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Work
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.clockinpro.R

data class TargetIconOption(
    val key: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector
)

data class TargetColorOption(
    val key: String,
    @StringRes val labelRes: Int,
    val color: Color
)

val targetIconOptions = listOf(
    TargetIconOption("work", R.string.target_icon_work, Icons.Default.Work),
    TargetIconOption("book", R.string.target_icon_read, Icons.Default.MenuBook),
    TargetIconOption("fitness", R.string.target_icon_fitness, Icons.Default.FitnessCenter),
    TargetIconOption("heart", R.string.target_icon_health, Icons.Default.Favorite),
    TargetIconOption("alarm", R.string.target_icon_routine, Icons.Default.Alarm),
    TargetIconOption("check", R.string.target_icon_general, Icons.Default.CheckCircle)
)

val targetColorOptions = listOf(
    TargetColorOption("ocean", R.string.target_color_ocean, Color(0xFF277DA1)),
    TargetColorOption("sunrise", R.string.target_color_sunrise, Color(0xFFF9844A)),
    TargetColorOption("forest", R.string.target_color_forest, Color(0xFF43AA8B)),
    TargetColorOption("berry", R.string.target_color_berry, Color(0xFFC06C84)),
    TargetColorOption("slate", R.string.target_color_slate, Color(0xFF4D6272)),
    TargetColorOption("gold", R.string.target_color_gold, Color(0xFFE9C46A))
)

fun iconForKey(key: String): ImageVector =
    targetIconOptions.firstOrNull { it.key == key }?.icon ?: Icons.Default.CheckCircle

fun colorForKey(key: String): Color =
    targetColorOptions.firstOrNull { it.key == key }?.color ?: targetColorOptions.first().color
