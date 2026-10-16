package com.personal.ytcompanion.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class ThemeSettings(
    val accentSeed: Long = AccentPresets.BLUE,
    val dynamicColor: Boolean = false,
    // Dark theme is fixed ON for Phase 1 per requirement (modern dark UI).
    val darkMode: Boolean = true
)

object AccentPresets {
    const val RED = 0xFFE53935L
    const val ORANGE = 0xFFFF8F00L
    const val GREEN = 0xFF00C853L
    const val BLUE = 0xFF2962FFL
    const val PURPLE = 0xFFAA00FFL
    const val PINK = 0xFFEC407AL
    const val TEAL = 0xFF00BFA5L

    val all = listOf(RED, ORANGE, GREEN, BLUE, PURPLE, PINK, TEAL)
}

class ThemePrefs(private val store: DataStore<Preferences>) {
    private object Keys {
        val ACCENT = longPreferencesKey("accent_seed")
        val DYNAMIC = booleanPreferencesKey("dynamic_color")
    }

    val settings: Flow<ThemeSettings> = store.data.map { p ->
        ThemeSettings(
            accentSeed = p[Keys.ACCENT] ?: AccentPresets.BLUE,
            dynamicColor = p[Keys.DYNAMIC] ?: false,
            darkMode = true
        )
    }

    suspend fun setAccent(seed: Long) {
        store.edit { it[Keys.ACCENT] = seed }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        store.edit { it[Keys.DYNAMIC] = enabled }
    }
}
