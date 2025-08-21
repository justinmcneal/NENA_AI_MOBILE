package com.example.nenaai.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryGreen,
    tertiary = AccentYellow,
    background = DarkBackground,
    surface = SurfaceDark,
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onTertiary = TextBlack,
    onBackground = TextWhite,
    onSurface = TextWhite,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryGreen,
    tertiary = AccentYellow,
    background = LightBackground,
    surface = SurfaceLight,
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onTertiary = TextBlack,
    onBackground = TextBlack,
    onSurface = TextBlack,
)

@Composable
fun NENA_AI_MOBILETheme( // Renamed for clarity
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.  statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
