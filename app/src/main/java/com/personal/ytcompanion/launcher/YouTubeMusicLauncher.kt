package com.personal.ytcompanion.launcher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

/**
 * Launcher gateway for the OFFICIAL YouTube Music app.
 *
 * Compliance: only fires an explicit launch Intent / Play Store / official
 * website. Never extracts audio, never blocks ads, never uses WebView login,
 * never stores Google credentials.
 */
class YouTubeMusicLauncher {

    sealed interface LaunchResult {
        data object OpenedApp : LaunchResult
        data object OpenedStore : LaunchResult
        data object OpenedWeb : LaunchResult
        data object Failed : LaunchResult
    }

    fun isInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(PACKAGE, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun appLaunchIntent(context: Context): Intent? =
        context.packageManager.getLaunchIntentForPackage(PACKAGE)

    fun open(context: Context): LaunchResult {
        // 1. Official app
        val appIntent = appLaunchIntent(context)
        if (appIntent != null) {
            appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return try {
                context.startActivity(appIntent)
                LaunchResult.OpenedApp
            } catch (_: Exception) {
                openStore(context)
            }
        }
        // 2. Play Store fallback
        return openStore(context)
    }

    private fun openStore(context: Context): LaunchResult {
        return try {
            val storeIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(PLAY_STORE_URL)
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(storeIntent)
            LaunchResult.OpenedStore
        } catch (_: Exception) {
            openWeb(context)
        }
    }

    private fun openWeb(context: Context): LaunchResult {
        return try {
            CustomTabsIntent.Builder().build().launchUrl(
                context,
                Uri.parse(WEB_URL)
            )
            LaunchResult.OpenedWeb
        } catch (_: Exception) {
            LaunchResult.Failed
        }
    }

    companion object {
        const val PACKAGE = "com.google.android.apps.youtube.music"
        const val PLAY_STORE_URL =
            "https://play.google.com/store/apps/details?id=com.google.android.apps.youtube.music"
        const val WEB_URL = "https://music.youtube.com"

        // Pure string helpers — JVM unit testable (no Android framework).
        fun buildPlayStoreUrl(): String = PLAY_STORE_URL
        fun buildWebUrl(): String = WEB_URL

        // Android Uri helpers — used by app code only, not by JVM tests.
        fun buildPlayStoreUri(): Uri = Uri.parse(PLAY_STORE_URL)
        fun buildWebUri(): Uri = Uri.parse(WEB_URL)
    }
}
