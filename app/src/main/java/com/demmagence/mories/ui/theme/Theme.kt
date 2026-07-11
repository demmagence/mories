package com.demmagence.mories.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MoriesDarkColorScheme = darkColorScheme(
    primary = MoriesPrimary,
    onPrimary = MoriesOnBackground,
    primaryContainer = MoriesPrimaryDark,
    onPrimaryContainer = MoriesOnBackground,
    secondary = MoriesGold,
    onSecondary = MoriesBackground,
    background = MoriesBackground,
    onBackground = MoriesOnBackground,
    surface = MoriesSurface,
    onSurface = MoriesOnSurface,
    surfaceVariant = MoriesSurfaceVariant,
    onSurfaceVariant = MoriesOnSurfaceVariant,
    error = MoriesError,
    onError = MoriesOnBackground,
    outline = MoriesTextTertiary,
    outlineVariant = MoriesSurfaceElevated
)

@Composable
fun MoriesTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MoriesBackground.toArgb()
            window.navigationBarColor = MoriesBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = MoriesDarkColorScheme,
        typography = MoriesTypography,
        shapes = MoriesShapes,
        content = content
    )
}
