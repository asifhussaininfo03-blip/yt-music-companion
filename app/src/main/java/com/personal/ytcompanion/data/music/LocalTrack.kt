package com.personal.ytcompanion.data.music

/** Pure model — no Android Uri so it stays unit-testable on the JVM. */
data class LocalTrack(
    val id: Long,
    val title: String,
    val artist: String,
    val durationMs: Long
) {
    fun isPlayable(): Boolean = durationMs > 0
}

fun List<LocalTrack>.playable(): List<LocalTrack> = filter { it.isPlayable() }
