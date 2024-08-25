package ir.atefehtaheri.musicbox.data.musiclist.local

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.atefehtaheri.musicbox.R
import ir.atefehtaheri.musicbox.core.common.madels.ResultStatus
import ir.atefehtaheri.musicbox.data.musiclist.local.models.MusicDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class LocalMusicListDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatcher: CoroutineDispatcher,
) : MusicListDataSource {

    override suspend fun getLocalMusics(): ResultStatus<List<MusicDto>> {
        val musicList = mutableListOf<MusicDto>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATA,

            )
        return withContext(dispatcher) {
            ensureActive()

            try {
                val cursor = context.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
                )

                cursor?.use {
                    val idIndex = it.getColumnIndex(MediaStore.Audio.Media._ID)
                    val titleIndex = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
                    val artistIndex = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                    val durationIndex = it.getColumnIndex(MediaStore.Audio.Media.DURATION)
                    val albumIdIndex = it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                    val dataIndex = it.getColumnIndex(MediaStore.Audio.Media.DATA)

                    while (it.moveToNext()) {
                        val id = it.getLong(idIndex)
                        val title = it.getString(titleIndex)
                        val artist = it.getString(artistIndex)
                        val duration = it.getLong(durationIndex)
                        val albumId = it.getLong(albumIdIndex)
                        val albumArtUri = getAlbumUri(context, albumId)
                        val filePath = it.getString(dataIndex)

                        musicList.add(
                            MusicDto(id, title, artist, duration, albumArtUri?.toString(), filePath)
                        )
                    }
                    ResultStatus.Success(musicList)

                } ?: ResultStatus.Failure(context.getString(R.string.cursor_error))

            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                } else {
                    ResultStatus.Failure(context.getString(R.string.cursor_error))
                }
            }
        }
    }

    private fun getAlbumUri(context: Context, albumId: Long): Uri {
        val albumArtUri = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(albumArtUri, albumId)
    }
}