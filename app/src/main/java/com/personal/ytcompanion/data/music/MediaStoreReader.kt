package com.personal.ytcompanion.data.music

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MediaStoreReader {

    fun contentUriFor(id: Long): Uri =
        ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
        )

    suspend fun query(context: Context): List<LocalTrack> =
        withContext(Dispatchers.IO) {
            val out = mutableListOf<LocalTrack>()
            val collection =
                if (Build.VERSION.SDK_INT >= 29)
                    MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                else MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val proj = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION
            )
            val sel = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            context.contentResolver.query(
                collection, proj, sel, null,
                "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
            )?.use { c ->
                val iId = c.getColumnIndexOrThrow(proj[0])
                val iName = c.getColumnIndexOrThrow(proj[1])
                val iArtist = c.getColumnIndexOrThrow(proj[2])
                val iDur = c.getColumnIndexOrThrow(proj[3])
                while (c.moveToNext()) {
                    out += LocalTrack(
                        id = c.getLong(iId),
                        title = c.getString(iName) ?: "Unknown",
                        artist = c.getString(iArtist) ?: "Unknown",
                        durationMs = try {
                            c.getLong(iDur)
                        } catch (_: Exception) {
                            0L
                        }
                    )
                }
            }
            out.playable()
        }
}
