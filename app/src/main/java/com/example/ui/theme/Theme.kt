package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PedalOrange,
    onPrimary = Color(0xFF1B1109),
    primaryContainer = PedalOrangeContainer,
    onPrimaryContainer = PedalOnOrangeContainer,
    secondary = PedalOrange,
    onSecondary = Color(0xFF1B1109),
    secondaryContainer = PedalCardElevated,
    onSecondaryContainer = PedalTextPrimary,
    tertiary = ModulePRE,
    background = PedalDarkBg,
    onBackground = PedalTextPrimary,
    surface = PedalCardBg,
    onSurface = PedalTextPrimary,
    surfaceVariant = PedalCardElevated,
    onSurfaceVariant = PedalTextSecondary,
    outline = PedalBorder,
    outlineVariant = Color(0xFF26282E)
)

private val LightColorScheme = lightColorScheme(
    primary = PedalOrangeDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCF),
    onPrimaryContainer = Color(0xFF380C00),
    secondary = PedalOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF0EAE1),
    onSecondaryContainer = Color(0xFF211A16),
    tertiary = Color(0xFF00687A),
    background = Color(0xFF121316), // keep sleek dark theme for pedal look
    onBackground = PedalTextPrimary,
    surface = Color(0xFF1B1D22),
    onSurface = PedalTextPrimary,
    surfaceVariant = Color(0xFF262930),
    onSurfaceVariant = Color(0xFFC4C6D0),
    outline = Color(0xFF454850),
    outlineVariant = Color(0xFF32353C)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent pro audio stompbox dark aesthetics
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
