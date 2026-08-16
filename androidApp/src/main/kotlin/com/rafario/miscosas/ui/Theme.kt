package com.rafario.miscosas.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = OrangePastel300,
    onPrimary = Black,
    primaryContainer = OrangePastel700,
    onPrimaryContainer = OrangePastel100,
    secondary = White,
    onSecondary = Black,
    secondaryContainer = CocoaMuted,
    onSecondaryContainer = OrangePastel100,
    tertiary = OrangePastel200,
    onTertiary = Black,
    background = Black,
    onBackground = OrangePastel100,
    surface = CocoaSurface,
    onSurface = OrangePastel100,
    surfaceVariant = CocoaMuted,
    onSurfaceVariant = OrangePastel200,
    outline = WarmOutline
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