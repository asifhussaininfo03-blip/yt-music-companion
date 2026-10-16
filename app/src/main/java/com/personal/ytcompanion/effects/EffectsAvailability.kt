package com.personal.ytcompanion.effects

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class EffectsSupport(
    val equalizerSupported: Boolean,
    val bassSupported: Boolean,
    val bandCount: Int = 0,
    /** Millibel range, e.g. -1500..1500. */
    val minBandMb: Int = 0,
    val maxBandMb: Int = 0,
    /** Center freq per band in mHz (for labels). */
    val centerFreqsMhz: List<Int> = emptyList(),
    /** Human-readable reason when unsupported. Null when OK. */
    val reason: String? = null
) {
    val anySupported: Boolean get() = equalizerSupported || bassSupported
}

object EffectsAvailability {

    suspend fun probe(audioSessionId: Int): EffectsSupport =
        withContext(Dispatchers.IO) {
            if (audioSessionId == 0) {
                return@withContext EffectsSupport(
                    equalizerSupported = false,
                    bassSupported = false,
                    reason = "Player is not ready yet (audio session 0). " +
                        "Play a local track first, then reopen the equalizer."
                )
            }
            var eqOk = false
            var bandCount = 0
            var minMb = 0
            var maxMb = 0
            var freqs: List<Int> = emptyList()
            try {
                val eq = Equalizer(0, audioSessionId)
                try {
                    bandCount = eq.numberOfBands.toInt()
                    val range = eq.bandLevelRange // [min, max] in mB
                    if (range != null && range.size >= 2) {
                        minMb = range[0].toInt()
                        maxMb = range[1].toInt()
                    }
                    freqs = (0 until bandCount).map { eq.getCenterFreq(it.toShort()) }
                    eqOk = bandCount > 0
                } finally {
                    eq.release()
                }
            } catch (_: UnsupportedOperationException) {
                eqOk = false
            } catch (_: IllegalArgumentException) {
                eqOk = false
            } catch (_: RuntimeException) {
                eqOk = false
            }

            var bassOk = false
            try {
                val bb = BassBoost(0, audioSessionId)
                try {
                    bassOk = true
                } finally {
                    bb.release()
                }
            } catch (_: UnsupportedOperationException) {
                bassOk = false
            } catch (_: IllegalArgumentException) {
                bassOk = false
            } catch (_: RuntimeException) {
                bassOk = false
            }

            if (!eqOk && !bassOk) {
                EffectsSupport(
                    equalizerSupported = false,
                    bassSupported = false,
                    reason = "Audio effects are not supported on this device " +
                        "or are blocked by the system. Local playback still works — " +
                        "only EQ/bass are disabled."
                )
            } else {
                EffectsSupport(
                    equalizerSupported = eqOk,
                    bassSupported = bassOk,
                    bandCount = bandCount,
                    minBandMb = minMb,
                    maxBandMb = maxMb,
                    centerFreqsMhz = freqs,
                    reason = when {
                        !eqOk -> "Equalizer not supported here; bass may still work."
                        !bassOk -> "Bass boost not supported here; equalizer still works."
                        else -> null
                    }
                )
            }
        }
}
