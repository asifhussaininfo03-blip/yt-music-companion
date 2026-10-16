package com.personal.ytcompanion.effects

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer

/**
 * Binds Equalizer + BassBoost to ONE ExoPlayer audio session.
 * Every call is guarded — returns false instead of crashing when
 * the device does not support the effect.
 */
class EffectsController {

    private var eq: Equalizer? = null
    private var bass: BassBoost? = null
    var sessionId: Int = 0
        private set

    fun bind(sessionId: Int): Boolean {
        release()
        if (sessionId == 0) return false
        this.sessionId = sessionId
        var ok = false
        try {
            eq = Equalizer(0, sessionId).also { it.enabled = true }
            ok = true
        } catch (_: Exception) {
            eq = null
        }
        try {
            bass = BassBoost(0, sessionId).also { it.enabled = true }
            ok = true
        } catch (_: Exception) {
            bass = null
        }
        return ok
    }

    fun setMasterEnabled(enabled: Boolean) {
        try {
            eq?.enabled = enabled
        } catch (_: Exception) { }
        try {
            bass?.enabled = enabled
        } catch (_: Exception) { }
    }

    /** levelMb in millibels, already clamped by caller. */
    fun setBandLevel(band: Int, levelMb: Int): Boolean {
        val e = eq ?: return false
        return try {
            e.setBandLevel(band.toShort(), levelMb.toShort())
            true
        } catch (_: Exception) {
            false
        }
    }

    /** strength 0..1000. */
    fun setBassStrength(strength: Int): Boolean {
        val b = bass ?: return false
        return try {
            b.setStrength(strength.toShort().coerceIn(0, 1000))
            true
        } catch (_: Exception) {
            false
        }
    }

    fun release() {
        try {
            eq?.release()
        } catch (_: Exception) { }
        try {
            bass?.release()
        } catch (_: Exception) { }
        eq = null
        bass = null
        sessionId = 0
    }
}
