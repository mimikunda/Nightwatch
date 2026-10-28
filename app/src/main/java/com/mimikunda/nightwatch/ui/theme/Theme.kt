package com.mimikunda.nightwatch.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val PitchBlackColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF00325B),
    primaryContainer = Color(0xFF004881),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFB0BEC5),
    onSecondary = Color(0xFF1C333E),
    secondaryContainer = Color(0xFF324A55),
    onSecondaryContainer = Color(0xFFCCE8F4),
    tertiary = Color(0xFFFFB74D),
    onTertiary = Color(0xFF4A2800),
    tertiaryContainer = Color(0xFF693C00),
    onTertiaryContainer = Color(0xFFFFDCC2),
    background = PitchBlack,
    onBackground = Color(0xFFE1E2E5),
    surface = PitchBlack,
    onSurface = Color(0xFFE1E2E5),
    surfaceVariant = CardSurfaceDark,
    onSurfaceVariant = Color(0xFFC3C6CF),
    outline = TableSurfaceBorder
)

@Composable
fun NightwatchTheme(
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val baseColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            dynamicDarkColorScheme(context)
        }
        else -> PitchBlackColorScheme
    }

    // Force Pitch Black background & surface for true AMOLED dark mode
    val amoledColorScheme = baseColorScheme.copy(
        background = PitchBlack,
        surface = PitchBlack
    )

    MaterialTheme(
        colorScheme = amoledColorScheme,
        typography = Typography,
        content = content
    )
}
