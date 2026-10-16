package com.personal.ytcompanion

import com.personal.ytcompanion.data.prefs.EqMath
import org.junit.Assert.assertEquals
import org.junit.Test

class EqMathTest {
    @Test
    fun bass_clampedTo0_1000() {
        assertEquals(0, EqMath.clampBass(-50))
        assertEquals(1000, EqMath.clampBass(5000))
        assertEquals(500, EqMath.clampBass(500))
    }

    @Test
    fun bands_roundTrip_and_fit() {
        val levels = listOf(100, -200, 300)
        val raw = EqMath.encodeBands(levels)
        assertEquals(levels, EqMath.decodeBands(raw))
        assertEquals(
            listOf(100, -200, 300, 0, 0),
            EqMath.fitToBandCount(levels, 5)
        )
        assertEquals(listOf(100, -200), EqMath.fitToBandCount(levels, 2))
    }
}
