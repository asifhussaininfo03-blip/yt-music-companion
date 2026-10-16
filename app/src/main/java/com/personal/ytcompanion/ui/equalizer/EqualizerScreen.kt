package com.personal.ytcompanion.ui.equalizer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.personal.ytcompanion.data.prefs.EqMath

private fun freqLabel(mhz: Int): String {
    val hz = mhz / 1000
    return if (hz >= 1000) "${hz / 1000}kHz" else "${hz}Hz"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerScreen(
    vm: EqualizerViewModel,
    onBack: () -> Unit
) {
    val support by vm.support.collectAsState()
    val saved by vm.saved.collectAsState()
    val note by vm.appliedNote.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Equalizer + Bass") }) }) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Applies to the local player only — never to YouTube Music.",
                style = MaterialTheme.typography.bodySmall
            )
            val sup = support
            if (sup == null) {
                CircularProgressIndicator()
                Text("Checking what this device supports…")
            } else if (!sup.anySupported) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Audio effects unavailable",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(sup.reason ?: "Not supported.")
                        Text(
                            "Local playback still works. Only EQ/bass are disabled.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                if (sup.reason != null) {
                    Card(Modifier.fillMaxWidth()) {
                        Text(sup.reason!!, Modifier.padding(12.dp))
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Enable effects", Modifier.weight(1f))
                    Switch(checked = saved.enabled, onCheckedChange = vm::setEnabled)
                }
                if (sup.equalizerSupported) {
                    Text("Bands", style = MaterialTheme.typography.titleSmall)
                    val fitted = EqMath.fitToBandCount(saved.bandLevelsMb, sup.bandCount)
                    fitted.forEachIndexed { i, mb ->
                        Column {
                            val label = sup.centerFreqsMhz.getOrNull(i)
                                ?.let { freqLabel(it) } ?: "Band ${i + 1}"
                            Text(
                                "$label  ${mb / 100}dB",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Slider(
                                value = mb.toFloat(),
                                onValueChange = { vm.setBand(i, it.toInt()) },
                                valueRange = sup.minBandMb.toFloat()..sup.maxBandMb.toFloat(),
                                enabled = saved.enabled
                            )
                        }
                    }
                }
                if (sup.bassSupported) {
                    Text("Bass boost: ${saved.bassStrength}/1000")
                    Slider(
                        value = saved.bassStrength.toFloat(),
                        onValueChange = { vm.setBass(it.toInt()) },
                        valueRange = 0f..1000f,
                        enabled = saved.enabled
                    )
                }
                if (note != null) Text(note!!)
                Button(onClick = { vm.reset() }) { Text("Reset") }
            }
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Back")
            }
        }
    }
}
