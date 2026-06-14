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
    primary = FutonDarkPrimary,
    onPrimary = FutonDarkOnPrimary,
    primaryContainer = FutonDarkPrimaryContainer,
    onPrimaryContainer = FutonDarkOnPrimaryContainer,
    inversePrimary = FutonDarkInversePrimary,
    secondary = FutonDarkSecondary,
    onSecondary = FutonDarkOnSecondary,
    secondaryContainer = FutonDarkSecondaryContainer,
    onSecondaryContainer = FutonDarkOnSecondaryContainer,
    tertiary = FutonDarkTertiary,
    onTertiary = FutonDarkOnTertiary,
    tertiaryContainer = FutonDarkTertiaryContainer,
    onTertiaryContainer = FutonDarkOnTertiaryContainer,
    background = FutonDarkBackground,
    onBackground = FutonDarkOnSurface,
    surface = FutonDarkSurface,
    onSurface = FutonDarkOnSurface,
    surfaceVariant = FutonDarkSurfaceVariant,
    onSurfaceVariant = FutonDarkOnSurfaceVariant,
    surfaceTint = FutonDarkPrimary,
    inverseSurface = FutonDarkInverseSurface,
    inverseOnSurface = FutonDarkInverseOnSurface,
    outline = FutonDarkOutline,
    outlineVariant = FutonDarkOutline,
    surfaceDim = FutonDarkSurfaceDim,
    surfaceBright = FutonDarkSurfaceBright,
    surfaceContainerLowest = FutonDarkSurfaceContainerLowest,
    surfaceContainerLow = FutonDarkSurfaceContainerLow,
    surfaceContainer = FutonDarkSurfaceContainer,
    surfaceContainerHigh = FutonDarkSurfaceContainerHigh,
    surfaceContainerHighest = FutonDarkSurfaceContainerHighest,
)

private val LightColorScheme = lightColorScheme(
    primary = FutonLightPrimary,
    onPrimary = FutonLightOnPrimary,
    primaryContainer = FutonLightPrimaryContainer,
    onPrimaryContainer = FutonLightOnPrimaryContainer,
    inversePrimary = FutonLightInversePrimary,
    secondary = FutonLightSecondary,
    onSecondary = FutonLightOnSecondary,
    secondaryContainer = FutonLightSecondaryContainer,
    onSecondaryContainer = FutonLightOnSecondaryContainer,
    tertiary = FutonLightTertiary,
    onTertiary = FutonLightOnTertiary,
    tertiaryContainer = FutonLightTertiaryContainer,
    onTertiaryContainer = FutonLightOnTertiaryContainer,
    background = FutonLightBackground,
    onBackground = FutonLightOnSurface,
    surface = FutonLightSurface,
    onSurface = FutonLightOnSurface,
    surfaceVariant = FutonLightSurfaceVariant,
    onSurfaceVariant = FutonLightOnSurfaceVariant,
    surfaceTint = FutonLightPrimary,
    inverseSurface = FutonLightInverseSurface,
    inverseOnSurface = FutonLightInverseOnSurface,
    outline = FutonLightOutline,
    outlineVariant = FutonLightOutline,
    surfaceDim = FutonLightSurfaceDim,
    surfaceBright = FutonLightSurfaceBright,
    surfaceContainerLowest = FutonLightSurfaceContainerLowest,
    surfaceContainerLow = FutonLightSurfaceContainerLow,
    surfaceContainer = FutonLightSurfaceContainer,
    surfaceContainerHigh = FutonLightSurfaceContainerHigh,
    surfaceContainerHighest = FutonLightSurfaceContainerHighest,
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
