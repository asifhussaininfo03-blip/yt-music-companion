package com.personal.ytcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personal.ytcompanion.app.YTCompanionApp
import com.personal.ytcompanion.launcher.YouTubeMusicLauncher
import com.personal.ytcompanion.ui.dashboard.DashboardScreen
import com.personal.ytcompanion.ui.dashboard.DashboardViewModel
import com.personal.ytcompanion.ui.equalizer.EqualizerScreen
import com.personal.ytcompanion.ui.equalizer.EqualizerViewModel
import com.personal.ytcompanion.ui.player.PlayerScreen
import com.personal.ytcompanion.ui.player.PlayerViewModel
import com.personal.ytcompanion.ui.settings.SettingsScreen
import com.personal.ytcompanion.ui.theme.YTCompanionTheme

private enum class Screen { Dashboard, Player, Equalizer, Settings }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as YTCompanionApp).container
        setContent {
            val vm: DashboardViewModel = viewModel(
                factory = DashboardViewModel.Factory(container.themePrefs)
            )
            val theme by vm.theme.collectAsState()
            YTCompanionTheme(settings = theme) {
                var screen by remember { mutableStateOf(Screen.Dashboard) }
                var status by remember { mutableStateOf<String?>(null) }
                val installed = remember {
                    container.ytLauncher.isInstalled(this)
                }
                when (screen) {
                    Screen.Settings -> SettingsScreen(
                        theme = theme,
                        onAccent = vm::setAccent,
                        onDynamic = vm::setDynamic,
                        onBack = { screen = Screen.Dashboard }
                    )
                    Screen.Player -> {
                        val pvm: PlayerViewModel = viewModel(
                            factory = PlayerViewModel.Factory(applicationContext)
                        )
                        PlayerScreen(
                            vm = pvm,
                            onBack = { screen = Screen.Dashboard },
                            onOpenEq = { screen = Screen.Equalizer }
                        )
                    }
                    Screen.Equalizer -> {
                        val evm: EqualizerViewModel = viewModel(
                            factory = EqualizerViewModel.Factory(
                                applicationContext, container.eqPrefs
                            )
                        )
                        EqualizerScreen(vm = evm) { screen = Screen.Player }
                    }
                    Screen.Dashboard -> DashboardScreen(
                        theme = theme,
                        ytInstalled = installed,
                        statusMessage = status,
                        onOpenYT = {
                            status = when (container.ytLauncher.open(this)) {
                                YouTubeMusicLauncher.LaunchResult.OpenedApp -> "Opening official app…"
                                YouTubeMusicLauncher.LaunchResult.OpenedStore -> "App not found — Play Store opened."
                                YouTubeMusicLauncher.LaunchResult.OpenedWeb -> "Store unavailable — website opened."
                                YouTubeMusicLauncher.LaunchResult.Failed -> "Could not open anything. Install YouTube Music manually."
                            }
                        },
                        onAccent = vm::setAccent,
                        onOpenSettings = { screen = Screen.Settings },
                        onOpenPlayer = { screen = Screen.Player }
                    )
                }
            }
        }
    }
}
