package com.personal.ytcompanion.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.personal.ytcompanion.data.prefs.ThemeSettings

val AccentChoices = listOf(
    0xFFE53935L, 0xFFFF8F00L, 0xFF00C853L, 0xFF2962FFL,
    0xFFAA00FFL, 0xFFEC407AL, 0xFF00BFA5L
)

private fun seedScheme(seed: Long, dark: Boolean) =
    if (dark) darkColorScheme(
        primary = Color(seed),
        secondary = Color(seed).copy(alpha = 0.7f),
        tertiary = Color(seed).copy(alpha = 0.5f),
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E)
    ) else lightColorScheme(
        primary = Color(seed)
    )

@Composable
fun YTCompanionTheme(
    settings: ThemeSettings,
    content: @Composable () -> Unit
) {
    // Phase 1: always dark per requirement. Dynamic color wired in Phase 2.
    val scheme = seedScheme(settings.accentSeed, dark = true)
    MaterialTheme(colorScheme = scheme, content = content)
}
