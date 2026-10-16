package com.personal.ytcompanion

import com.personal.ytcompanion.launcher.YouTubeMusicLauncher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LauncherUriTest {
    @Test
    fun playStoreUrl_pointsToOfficialPackage() {
        assertEquals(
            "https://play.google.com/store/apps/details?id=com.google.android.apps.youtube.music",
            YouTubeMusicLauncher.buildPlayStoreUrl()
        )
    }

    @Test
    fun webUrl_isOfficialSite() {
        assertEquals(
            "https://music.youtube.com",
            YouTubeMusicLauncher.buildWebUrl()
        )
    }

    @Test
    fun packageName_isOfficialYtMusic() {
        assertEquals(
            "com.google.android.apps.youtube.music",
            YouTubeMusicLauncher.PACKAGE
        )
        assertTrue(
            YouTubeMusicLauncher.PLAY_STORE_URL.contains(YouTubeMusicLauncher.PACKAGE)
        )
    }
}
