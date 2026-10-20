package com.heavencanthandle.snake.ui

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Monochrome E-ink palette aligned with sibling Kompakt apps.
 * Start from [lightColorScheme] so newer Material 3 roles are never Unspecified/transparent.
 */
val monochrome = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    primaryContainer = Color.Black,
    onPrimaryContainer = Color.White,
    inversePrimary = Color.White,
    secondary = Color.Black,
    onSecondary = Color.White,
    secondaryContainer = Color.White,
    onSecondaryContainer = Color.Black,
    tertiary = Color.Black,
    onTertiary = Color.White,
    tertiaryContainer = Color.White,
    onTertiaryContainer = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color.White,
    onSurfaceVariant = Color.Black,
    surfaceTint = Color.Black,
    inverseSurface = Color.Black,
    inverseOnSurface = Color.White,
    error = Color.Black,
    onError = Color.White,
    errorContainer = Color.White,
    onErrorContainer = Color.Black,
    outline = Color.Black,
    outlineVariant = Color.Black,
    scrim = Color.Black,
    surfaceBright = Color.White,
    surfaceDim = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color.White,
    surfaceContainerHighest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainerLowest = Color.White,
)
