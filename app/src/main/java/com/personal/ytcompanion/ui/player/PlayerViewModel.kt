package com.personal.ytcompanion.ui.player

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.personal.ytcompanion.data.music.LocalTrack
import com.personal.ytcompanion.data.music.MediaStoreReader
import com.personal.ytcompanion.playback.PlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PlayerUiState(
    val tracks: List<LocalTrack> = emptyList(),
    val loading: Boolean = true,
    val currentIndex: Int = -1,
    val isPlaying: Boolean = false,
    val error: String? = null
)

class PlayerViewModel(private val appContext: Context) : ViewModel() {

    private val _ui = MutableStateFlow(PlayerUiState())
    val ui: StateFlow<PlayerUiState> = _ui

    private var controller: MediaController? = null

    init {
        connect()
        loadTracks()
    }

    private fun connect() {
        val token = SessionToken(
            appContext,
            ComponentName(appContext, PlaybackService::class.java)
        )
        val future = MediaController.Builder(appContext, token).buildAsync()
        future.addListener(
            { controller = try {
                future.get()
            } catch (_: Exception) {
                null
            } },
            MoreExecutors.directExecutor()
        )
    }

    fun loadTracks() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)
            try {
                val list = MediaStoreReader.query(appContext)
                _ui.value = _ui.value.copy(tracks = list, loading = false)
            } catch (e: SecurityException) {
                _ui.value = _ui.value.copy(
                    loading = false,
                    error = "Audio permission denied. Grant it to see local files."
                )
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(
                    loading = false, error = e.message ?: "Load failed"
                )
            }
        }
    }

    fun playAt(index: Int) {
        val tracks = _ui.value.tracks
        if (index !in tracks.indices) return
        val c = controller ?: return
        val items = tracks.map { t ->
            MediaItem.Builder()
                .setUri(MediaStoreReader.contentUriFor(t.id))
                .setMediaMetadata(
                    androidx.media3.common.MediaMetadata.Builder()
                        .setTitle(t.title).setArtist(t.artist).build()
                ).build()
        }
        c.setMediaItems(items, index, 0L)
        c.prepare()
        c.play()
        _ui.value = _ui.value.copy(currentIndex = index, isPlaying = true)
    }

    fun togglePlay() {
        val c = controller ?: return
        if (c.isPlaying) {
            c.pause()
            _ui.value = _ui.value.copy(isPlaying = false)
        } else {
            c.play()
            _ui.value = _ui.value.copy(isPlaying = true)
        }
    }

    override fun onCleared() {
        controller?.release()
        controller = null
        super.onCleared()
    }

    class Factory(private val ctx: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PlayerViewModel(ctx.applicationContext) as T
    }
}
