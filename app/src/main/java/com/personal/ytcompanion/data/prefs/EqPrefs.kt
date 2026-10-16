package com.personal.ytcompanion.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class EqSettings(
    val enabled: Boolean = true,
    /** 0..1000 as required by BassBoost.setStrength(). */
    val bassStrength: Int = 0,
    /** Millibels per band, size == device band count (default 5 x 0). */
    val bandLevelsMb: List<Int> = List(5) { 0 }
)

/** Pure math — JVM unit testable, no Android imports. */
object EqMath {
    fun clampBass(v: Int): Int = v.coerceIn(0, 1000)

    fun clampBand(levelMb: Int, minMb: Int, maxMb: Int): Int =
        levelMb.coerceIn(minMb, maxMb)

    fun encodeBands(levels: List<Int>): String = levels.joinToString(",")

    fun decodeBands(raw: String?, fallbackSize: Int = 5): List<Int> {
        if (raw.isNullOrBlank()) return List(fallbackSize) { 0 }
        val parsed = raw.split(",").mapNotNull { it.trim().toIntOrNull() }
        if (parsed.isEmpty()) return List(fallbackSize) { 0 }
        return parsed
    }

    /** Resize stored levels to the device's band count (pad with 0 / truncate). */
    fun fitToBandCount(levels: List<Int>, count: Int): List<Int> =
        List(count) { i -> if (i < levels.size) levels[i] else 0 }
}

class EqPrefs(private val store: DataStore<Preferences>) {
    private object Keys {
        val ENABLED = booleanPreferencesKey("eq_enabled")
        val BASS = intPreferencesKey("eq_bass")
        val BANDS = stringPreferencesKey("eq_bands_csv")
    }

    val settings: Flow<EqSettings> = store.data.map { p ->
        EqSettings(
            enabled = p[Keys.ENABLED] ?: true,
            bassStrength = EqMath.clampBass(p[Keys.BASS] ?: 0),
            bandLevelsMb = EqMath.decodeBands(p[Keys.BANDS])
        )
    }

    suspend fun setEnabled(v: Boolean) {
        store.edit { it[Keys.ENABLED] = v }
    }

    suspend fun setBass(v: Int) {
        store.edit { it[Keys.BASS] = EqMath.clampBass(v) }
    }

    suspend fun setBands(levels: List<Int>) {
        store.edit { it[Keys.BANDS] = EqMath.encodeBands(levels) }
    }

    suspend fun reset() {
        store.edit {
            it[Keys.ENABLED] = true
            it[Keys.BASS] = 0
            it[Keys.BANDS] = EqMath.encodeBands(List(5) { 0 })
        }
    }
}
