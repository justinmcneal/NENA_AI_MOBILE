package com.example.nenaai.ui.theme

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// BPI-inspired colors (primary red for accents/buttons, gold for highlights, neutrals for backgrounds/text)
private val BpiPrimary = Color(0xFFB11116)
private val BpiPrimaryDark = Color(0xFFE57373)
private val BpiSecondary = Color(0xFFD5B527)
private val BpiSecondaryDark = Color(0xFFFFE082)
private val BpiTertiary = Color(0xFF003B5C)
private val BpiTertiaryDark = Color(0xFF81D4FA)
private val BpiBackgroundLight = Color(0xFFFFFFFF)
private val BpiBackgroundDark = Color(0xFF121212)
private val BpiSurfaceLight = Color(0xFFF5F5F5)
private val BpiSurfaceDark = Color(0xFF1E1E1E)
private val BpiOnPrimary = Color(0xFFFFFFFF)
private val BpiOnSecondary = Color(0xFF000000)
private val BpiOnTertiary = Color(0xFFFFFFFF)
private val BpiOnBackgroundLight = Color(0xFF000000)
private val BpiOnBackgroundDark = Color(0xFFFFFFFF)
private val BpiOnSurfaceLight = Color(0xFF212121)
private val BpiOnSurfaceDark = Color(0xFFE0E0E0)

private val LightColorScheme = lightColorScheme(
    primary = BpiPrimary,
    secondary = BpiSecondary,
    tertiary = BpiTertiary,
    background = BpiBackgroundLight,
    surface = BpiSurfaceLight,
    onPrimary = BpiOnPrimary,
    onSecondary = BpiOnSecondary,
    onTertiary = BpiOnTertiary,
    onBackground = BpiOnBackgroundLight,
    onSurface = BpiOnSurfaceLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = BpiPrimaryDark,
    secondary = BpiSecondaryDark,
    tertiary = BpiTertiaryDark,
    background = BpiBackgroundDark,
    surface = BpiSurfaceDark,
    onPrimary = BpiOnPrimary,
    onSecondary = BpiOnSecondary,
    onTertiary = BpiOnTertiary,
    onBackground = BpiOnBackgroundDark,
    onSurface = BpiOnSurfaceDark,
)

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun NENA_AI_MOBILETheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
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