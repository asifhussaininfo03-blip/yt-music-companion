package com.personal.ytcompanion.app

import android.app.Application
import com.personal.ytcompanion.data.prefs.ThemePrefs
import com.personal.ytcompanion.di.AppContainer

class YTCompanionApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)
    }
}
