package com.clockinpro.v2.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.ConfigurationCompat
import java.util.Locale

@Composable
fun currentAppLocale(): Locale {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    return remember(configuration, context) {
        ConfigurationCompat.getLocales(context.resources.configuration).get(0) ?: Locale.getDefault()
    }
}
