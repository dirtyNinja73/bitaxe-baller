package com.bitaxeballer.mobile.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GitHubDarkColorScheme = darkColorScheme(
    primary = Color(0xFF58A6FF),
    onPrimary = Color(0xFF0D1117),
    secondary = Color(0xFF79C0FF),
    onSecondary = Color(0xFF0D1117),
    tertiary = Color(0xFFA5D6FF),
    onTertiary = Color(0xFF0D1117),
    background = Color(0xFF0D1117),
    onBackground = Color(0xFFC9D1D9),
    surface = Color(0xFF161B22),
    onSurface = Color(0xFFC9D1D9),
    surfaceVariant = Color(0xFF21262D),
    onSurfaceVariant = Color(0xFF8B949E),
    outline = Color(0xFF30363D),
    error = Color(0xFFF85149),
    onError = Color(0xFF0D1117)
)

@Composable
fun BitaxeBallerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GitHubDarkColorScheme,
        content = content
    )
}
