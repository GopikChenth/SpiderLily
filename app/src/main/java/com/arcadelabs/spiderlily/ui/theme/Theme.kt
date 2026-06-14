package com.arcadelabs.spiderlily.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SpiderRed,
    onPrimary = WarmIvory,
    primaryContainer = SpiderRedDeep,
    onPrimaryContainer = WarmIvory,
    secondary = SpiderGold,
    onSecondary = VelvetBlack,
    secondaryContainer = SurfaceHighest,
    onSecondaryContainer = WarmIvory,
    tertiary = WarmClay,
    background = VelvetBlack,
    onBackground = WarmIvory,
    surface = Obsidian,
    onSurface = WarmIvory,
    surfaceVariant = SurfaceRaised,
    onSurfaceVariant = WarmClay,
    outline = MutedText,
)

private val LightColorScheme = lightColorScheme(
    primary = SpiderRedDeep,
    onPrimary = WarmIvory,
    secondary = SpiderRed,
    tertiary = SpiderGold,
)

@Composable
fun SpiderlilyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
