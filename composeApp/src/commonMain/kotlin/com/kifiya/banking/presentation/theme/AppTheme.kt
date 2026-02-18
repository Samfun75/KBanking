package com.kifiya.banking.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryBlue = Color(0xFF1E3A5F)
val PrimaryBlueLight = Color(0xFF2E5077)
val PrimaryBlueDark = Color(0xFF0E2A4F)
val AccentGold = Color(0xFFD4AF37)
val AccentGoldLight = Color(0xFFE4BF47)
val SuccessGreen = Color(0xFF2E7D32)
val ErrorRed = Color(0xFFD32F2F)
val BackgroundLight = Color(0xFFF5F7FA)
val BackgroundDark = Color(0xFF121212)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF1E1E1E)
val TextPrimary = Color(0xFF212121)
val TextSecondary = Color(0xFF757575)
val TextOnPrimary = Color(0xFFFFFFFF)
val CreditGreen = Color(0xFF4CAF50)
val DebitRed = Color(0xFFE53935)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = TextOnPrimary,
    secondary = AccentGold,
    onSecondary = TextPrimary,
    secondaryContainer = AccentGoldLight,
    onSecondaryContainer = TextPrimary,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFE8EDF2),
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = TextOnPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueLight,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryBlue,
    onPrimaryContainer = TextOnPrimary,
    secondary = AccentGold,
    onSecondary = TextPrimary,
    secondaryContainer = AccentGoldLight,
    onSecondaryContainer = TextPrimary,
    background = BackgroundDark,
    onBackground = TextOnPrimary,
    surface = SurfaceDark,
    onSurface = TextOnPrimary,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB0B0B0),
    error = ErrorRed,
    onError = TextOnPrimary
)

@Composable
fun BankingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

