package com.personal.ytcompanion.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.personal.ytcompanion.data.prefs.EqPrefs
import com.personal.ytcompanion.data.prefs.ThemePrefs
import com.personal.ytcompanion.launcher.YouTubeMusicLauncher

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/** Simple manual DI to keep Phase 1 beginner-friendly (no Hilt yet). */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    val themePrefs = ThemePrefs(appContext.dataStore)
    val eqPrefs = EqPrefs(appContext.dataStore)
    val ytLauncher = YouTubeMusicLauncher()
}
