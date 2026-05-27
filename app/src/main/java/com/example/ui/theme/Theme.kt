package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OrinDarkColorScheme = darkColorScheme(
    primary = AccentPurple,
    secondary = AccentPurple,
    tertiary = ConsoleText,
    background = PureBlack,
    surface = NearBlack,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = PrimaryText,
    onSurface = PrimaryText,
    surfaceVariant = DarkCard,
    onSurfaceVariant = SecondaryText,
    outline = SubtleBorder,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark mode for developer aesthetic
    dynamicColor: Boolean = false, // Disable dynamic colors to protect Orin's core branding color palette
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OrinDarkColorScheme,
        typography = Typography,
        content = content
    )
}
