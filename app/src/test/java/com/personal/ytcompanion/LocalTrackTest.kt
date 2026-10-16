package com.personal.ytcompanion

import com.personal.ytcompanion.data.music.LocalTrack
import com.personal.ytcompanion.data.music.playable
import org.junit.Assert.assertEquals
import org.junit.Test

class LocalTrackTest {
    @Test
    fun zeroDuration_isFilteredOut() {
        val tracks = listOf(
            LocalTrack(1, "A", "X", 180_000),
            LocalTrack(2, "B", "Y", 0),
            LocalTrack(3, "C", "Z", -5)
        )
        assertEquals(1, tracks.playable().size)
        assertEquals("A", tracks.playable().first().title)
    }
}
