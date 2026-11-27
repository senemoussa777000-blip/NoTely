package com.notely.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = White,
    secondary = Mint,
    onSecondary = Midnight,
    tertiary = Peach,
    onTertiary = Midnight,
    background = Cream,
    onBackground = Midnight,
    surface = White,
    onSurface = Midnight,
    surfaceVariant = Cream,
    onSurfaceVariant = LightGrey,
    error = Color(0xFFD32F2F)
)

private val DarkColors = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = White,
    secondary = Mint,
    onSecondary = Midnight,
    tertiary = Peach,
    onTertiary = Midnight,
    background = Midnight,
    onBackground = Cream,
    surface = MidnightSurface,
    onSurface = Cream,
    surfaceVariant = MidnightSurfaceVariant,
    onSurfaceVariant = LightGrey,
    error = Color(0xFFEF5350)
)

/**
 * Thème NOTELY.
 * @param theme "system", "clair" ou "sombre" (préférence enregistrée dans DataStore).
 */
@Composable
fun NotelyTheme(
    theme: String = "system",
    content: @Composable () -> Unit
) {
    val darkTheme = when (theme) {
        "clair" -> false
        "sombre" -> true
        else -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = NotelyTypography,
        shapes = NotelyShapes,
        content = content
    )
}
