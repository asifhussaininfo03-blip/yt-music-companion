package com.personal.ytcompanion.ui.equalizer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.personal.ytcompanion.data.prefs.EqMath
import com.personal.ytcompanion.data.prefs.EqPrefs
import com.personal.ytcompanion.data.prefs.EqSettings
import com.personal.ytcompanion.effects.EffectsAvailability
import com.personal.ytcompanion.effects.EffectsController
import com.personal.ytcompanion.effects.EffectsSupport
import com.personal.ytcompanion.playback.PlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EqualizerViewModel(
    private val appContext: Context,
    private val prefs: EqPrefs
) : ViewModel() {

    private val controller = EffectsController()

    private val _support = MutableStateFlow<EffectsSupport?>(null)
    val support: StateFlow<EffectsSupport?> = _support

    private val _saved = MutableStateFlow(EqSettings())
    val saved: StateFlow<EqSettings> = _saved

    private val _appliedNote = MutableStateFlow<String?>(null)
    val appliedNote: StateFlow<String?> = _appliedNote

    init {
        viewModelScope.launch {
            _saved.value = prefs.settings.first()
            // Rebind whenever the player's audio session changes.
            PlaybackService.audioSessionId.collect { sid ->
                rebind(sid)
            }
        }
        viewModelScope.launch {
            prefs.settings.collect { _saved.value = it }
        }
    }

    private suspend fun rebind(sessionId: Int) {
        val sup = EffectsAvailability.probe(sessionId)
        _support.value = sup
        if (!sup.anySupported) return
        controller.bind(sessionId)
        applyAll(sup, _saved.value)
    }

    private fun applyAll(sup: EffectsSupport, s: EqSettings) {
        controller.setMasterEnabled(s.enabled)
        if (!s.enabled) return
        if (sup.equalizerSupported) {
            val fitted = EqMath.fitToBandCount(s.bandLevelsMb, sup.bandCount)
            fitted.forEachIndexed { i, mb ->
                controller.setBandLevel(
                    i, EqMath.clampBand(mb, sup.minBandMb, sup.maxBandMb)
                )
            }
        }
        if (sup.bassSupported) {
            controller.setBassStrength(EqMath.clampBass(s.bassStrength))
        }
    }

    fun setEnabled(v: Boolean) = viewModelScope.launch {
        prefs.setEnabled(v)
        controller.setMasterEnabled(v)
        if (v) {
            val sup = _support.value
            if (sup != null) applyAll(sup, _saved.value.copy(enabled = true))
        }
    }

    fun setBand(index: Int, levelMb: Int) = viewModelScope.launch {
        val sup = _support.value ?: return@launch
        val clamped = EqMath.clampBand(levelMb, sup.minBandMb, sup.maxBandMb)
        val fitted = EqMath.fitToBandCount(_saved.value.bandLevelsMb, sup.bandCount)
            .toMutableList()
        if (index in fitted.indices) {
            fitted[index] = clamped
            prefs.setBands(fitted)
            if (_saved.value.enabled) controller.setBandLevel(index, clamped)
        }
    }

    fun setBass(strength: Int) = viewModelScope.launch {
        val clamped = EqMath.clampBass(strength)
        prefs.setBass(clamped)
        if (_saved.value.enabled) {
            val ok = controller.setBassStrength(clamped)
            _appliedNote.value =
                if (ok) null else "Bass change was rejected by this device."
        }
    }

    fun reset() = viewModelScope.launch {
        prefs.reset()
        val sup = _support.value
        if (sup != null) applyAll(sup, EqSettings())
    }

    override fun onCleared() {
        controller.release()
        super.onCleared()
    }

    class Factory(
        private val ctx: Context,
        private val prefs: EqPrefs
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            EqualizerViewModel(ctx.applicationContext, prefs) as T
    }
}
