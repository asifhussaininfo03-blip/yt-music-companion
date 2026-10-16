package com.personal.ytcompanion.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.personal.ytcompanion.data.prefs.ThemeSettings
import com.personal.ytcompanion.ui.theme.AccentChoices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    theme: ThemeSettings,
    ytInstalled: Boolean,
    statusMessage: String?,
    onOpenYT: () -> Unit,
    onAccent: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenPlayer: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("YT Companion") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        if (ytInstalled) "YouTube Music detected."
                        else "YouTube Music is not installed. You will be sent to the Play Store.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onOpenYT, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Text("  Open YouTube Music")
                    }
                    if (statusMessage != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(statusMessage, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Text("Accent color", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AccentChoices.forEach { seed ->
                    val selected = theme.accentSeed == seed
                    Box(
                        modifier = Modifier
                            .size(if (selected) 40.dp else 32.dp)
                            .clip(CircleShape)
                            .background(Color(seed))
                            .clickable { onAccent(seed) }
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null)
                    Column(Modifier.padding(start = 12.dp).weight(1f)) {
                        Text("Local player — offline, independent")
                        Text(
                            "Plays your own files. Separate from YouTube Music.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Button(onClick = onOpenPlayer) { Text("Open") }
                }
            }

            Text(
                "This app only launches the official YouTube Music app. " +
                    "It does not download, extract, or strip ads from YouTube content.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
