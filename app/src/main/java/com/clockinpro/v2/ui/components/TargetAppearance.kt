package com.clockinpro.v2.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class TargetIconOption(
    val key: String,
    val label: String,
    val icon: ImageVector
)

data class TargetColorOption(
    val key: String,
    val label: String,
    val color: Color
)

val targetIconOptions = listOf(
    TargetIconOption("work", "Work", Icons.Default.Work),
    TargetIconOption("book", "Read", Icons.Default.MenuBook),
    TargetIconOption("fitness", "Fitness", Icons.Default.FitnessCenter),
    TargetIconOption("heart", "Health", Icons.Default.Favorite),
    TargetIconOption("alarm", "Routine", Icons.Default.Alarm),
    TargetIconOption("check", "General", Icons.Default.CheckCircle)
)

val targetColorOptions = listOf(
    TargetColorOption("ocean", "Ocean", Color(0xFF277DA1)),
    TargetColorOption("sunrise", "Sunrise", Color(0xFFF9844A)),
    TargetColorOption("forest", "Forest", Color(0xFF43AA8B)),
    TargetColorOption("berry", "Berry", Color(0xFFC06C84)),
    TargetColorOption("slate", "Slate", Color(0xFF4D6272)),
    TargetColorOption("gold", "Gold", Color(0xFFE9C46A))
)

fun iconForKey(key: String): ImageVector =
    targetIconOptions.firstOrNull { it.key == key }?.icon ?: Icons.Default.CheckCircle

fun colorForKey(key: String): Color =
    targetColorOptions.firstOrNull { it.key == key }?.color ?: targetColorOptions.first().color
