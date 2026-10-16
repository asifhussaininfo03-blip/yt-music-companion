package com.personal.ytcompanion.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.personal.ytcompanion.data.prefs.ThemePrefs
import com.personal.ytcompanion.data.prefs.ThemeSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(private val prefs: ThemePrefs) : ViewModel() {
    val theme: StateFlow<ThemeSettings> = prefs.settings.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000),
        ThemeSettings()
    )

    fun setAccent(seed: Long) = viewModelScope.launch { prefs.setAccent(seed) }
    fun setDynamic(enabled: Boolean) = viewModelScope.launch { prefs.setDynamicColor(enabled) }

    class Factory(private val prefs: ThemePrefs) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DashboardViewModel(prefs) as T
    }
}
