package ir.atefehtaheri.musicbox.data.musiclist.local

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Context
import android.content.IntentSender
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.atefehtaheri.musicbox.R
import ir.atefehtaheri.musicbox.core.common.models.ResultStatus
import ir.atefehtaheri.musicbox.data.musiclist.local.models.MusicDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LocalMusicListDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatcher: CoroutineDispatcher,
) : MusicListDataSource {

    private val projectionColumns = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.DATA
    )
    private val musicContentUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    private val selection = "${MediaStore.Audio.Media.IS_MUSIC} == 1"
    private fun getLocalMusics(): ResultStatus<List<MusicDto>> {
        return try {
            val cursor = context.contentResolver.query(
                musicContentUri,
                projectionColumns,
                selection,
                null,
                null
            )
            getCursorData(cursor)

        } catch (e: Exception) {
            ResultStatus.Failure(context.getString(R.string.cursor_error))
        }
    }

    private fun getCursorData(cursor: Cursor?): ResultStatus<List<MusicDto>> {
        val musicList = mutableListOf<MusicDto>()
        cursor?.use {
            if (it.count == 0) {
                return ResultStatus.Failure(context.getString(R.string.music_notfound))
            }
            while (it.moveToNext()) {
                musicList.add(it.asMusicDto())
            }
            return ResultStatus.Success(musicList)

        } ?: return ResultStatus.Failure(context.getString(R.string.cursor_error))
    }

    private fun Cursor.asMusicDto(): MusicDto {

        val id = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
        val title = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
        val artist = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
        val duration = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
        val albumId = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
        val filePath = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
        val albumArtUri = getAlbumUri(albumId)

        return MusicDto(id, title, artist, duration, albumArtUri.toString(), filePath)
    }


    private fun getAlbumUri(albumId: Long): Uri {
        val albumArtUri = Uri.parse(context.getString(R.string.albumart_uri))
        return ContentUris.withAppendedId(albumArtUri, albumId)
    }


    override fun getLocalMusicsFlow(): Flow<ResultStatus<List<MusicDto>>> = callbackFlow {
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                trySend(getLocalMusics())
            }
        }

        context.contentResolver.registerContentObserver(musicContentUri, true, observer)

        val initialMusic = getLocalMusics()
        trySend(initialMusic)

        awaitClose {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }.flowOn(dispatcher)

    override fun deleteMusic(idMusic: Long, handleException: (IntentSender) -> Unit) {

        try {
            val uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                idMusic
            )
            context.contentResolver.delete(uri, null, null)
        } catch (e: SecurityException) {
            val recoverableSecurityException = e as? RecoverableSecurityException
            val intentSender = recoverableSecurityException?.userAction?.actionIntent?.intentSender
            intentSender?.let {
                handleException(it)
            }
        }
    }
}