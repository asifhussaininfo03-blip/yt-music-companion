package com.personal.ytcompanion.ui.player

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    vm: PlayerViewModel,
    onBack: () -> Unit,
    onOpenEq: () -> Unit
) {
    val ui by vm.ui.collectAsState()
    val ctx = LocalContext.current
    val perm = if (Build.VERSION.SDK_INT >= 33)
        Manifest.permission.READ_MEDIA_AUDIO
    else Manifest.permission.READ_EXTERNAL_STORAGE
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) vm.loadTracks() }
    LaunchedEffect(Unit) {
        val has = ContextCompat.checkSelfPermission(ctx, perm) ==
            PackageManager.PERMISSION_GRANTED
        if (!has) launcher.launch(perm) else vm.loadTracks()
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Local player") }) }) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Offline only — plays files already on this device. " +
                    "Independent from YouTube Music.",
                style = MaterialTheme.typography.bodySmall
            )
            if (ui.loading) {
                CircularProgressIndicator()
            } else if (ui.error != null) {
                Text(ui.error!!)
                Button(onClick = { launcher.launch(perm) }) {
                    Text("Grant permission")
                }
            } else if (ui.tracks.isEmpty()) {
                Text("No local audio found. Copy MP3s into Music/ and retry.")
                Button(onClick = { vm.loadTracks() }) { Text("Rescan") }
            } else {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        "${ui.tracks.size} tracks",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleSmall
                    )
                    IconButton(onClick = { vm.togglePlay() }) {
                        Icon(
                            if (ui.isPlaying) Icons.Default.Pause
                            else Icons.Default.PlayArrow,
                            contentDescription = "play/pause"
                        )
                    }
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(ui.tracks) { i, t ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { vm.playAt(i) }
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    t.title,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "${t.artist} • ${t.durationMs / 1000}s" +
                                        if (i == ui.currentIndex) " • playing" else "",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
            Button(onClick = onOpenEq, modifier = Modifier.fillMaxWidth()) {
                Text("Equalizer + Bass")
            }
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Back to dashboard")
            }
        }
    }
}
