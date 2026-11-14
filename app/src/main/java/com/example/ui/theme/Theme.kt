package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DeepBlueAccentDark,
    onPrimary = DeepBluePrimary,
    primaryContainer = DeepBlueAccentSoftDark,
    onPrimaryContainer = DeepBlueAccentDark,
    secondary = DeepBlueAccentDark,
    onSecondary = Color.White,
    secondaryContainer = ObsidianContainerDark,
    onSecondaryContainer = ObsidianTextPrimaryDark,
    background = ObsidianBackgroundDark,
    onBackground = ObsidianTextPrimaryDark,
    surface = ObsidianCardDark,
    onSurface = ObsidianTextPrimaryDark,
    surfaceVariant = ObsidianContainerDark,
    onSurfaceVariant = ObsidianTextSecondaryDark,
    outline = ObsidianBorderDark,
    outlineVariant = ObsidianBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = DeepBluePrimary,
    onPrimary = Color.White,
    primaryContainer = DeepBlueAccentSoft,
    onPrimaryContainer = DeepBluePrimary,
    secondary = DeepBlueAccent,
    onSecondary = Color.White,
    secondaryContainer = LinenContainerLight,
    onSecondaryContainer = LinenTextPrimaryLight,
    background = LinenBackgroundLight,
    onBackground = LinenTextPrimaryLight,
    surface = LinenCardLight,
    onSurface = LinenTextPrimaryLight,
    surfaceVariant = LinenContainerLight,
    onSurfaceVariant = LinenTextSecondaryLight,
    outline = LinenBorderLight,
    outlineVariant = LinenBorderLight
)

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.LIGHT,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
