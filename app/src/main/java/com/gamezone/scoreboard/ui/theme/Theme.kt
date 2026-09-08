package com.gamezone.scoreboard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = BgColor,
    surface = SurfaceColor,
    surfaceVariant = Surface2Color,
    onBackground = InkColor,
    onSurface = InkColor,
    primary = GreenColor,
    error = RedColor,
    outline = LineColor
)

@Composable
fun ScoreboardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}