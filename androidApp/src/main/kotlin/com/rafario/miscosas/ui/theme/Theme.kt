package com.rafario.miscosas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDarkMode,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainerDarkMode,
    onPrimaryContainer = PrimaryDarkDarkMode,

    background = BackgroundDarkMode,
    onBackground = TextPrimaryDarkMode,

    surface = SurfaceDarkMode,
    onSurface = TextPrimaryDarkMode,

    surfaceVariant = SurfaceVariantDarkMode,
    onSurfaceVariant = TextSecondaryDarkMode,

    outline = OutlineDarkMode,
    outlineVariant = DividerDarkMode,

    error = ErrorDarkMode,
    onError = Color.White,
    errorContainer = ErrorContainerDarkMode,
    onErrorContainer = ErrorDarkMode,
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = PrimaryDark,

    background = Background,
    onBackground = TextPrimary,

    surface = Surface,
    onSurface = TextPrimary,

    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline = Outline,
    outlineVariant = Divider,

    error = Error,
    onError = Color.White,
    errorContainer = ErrorContainer,
    onErrorContainer = ErrorDark,
)

@Composable
fun MisCosasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}