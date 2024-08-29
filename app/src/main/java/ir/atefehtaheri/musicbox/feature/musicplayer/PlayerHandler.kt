package ir.atefehtaheri.musicbox.feature.musicplayer

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.atefehtaheri.musicbox.data.musiclist.local.models.MusicDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class PlayerHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaControllerFuture: ListenableFuture<MediaController>,
) {

    private val _mediaController: MutableStateFlow<MediaController?> = MutableStateFlow(null)
    val mediaController = _mediaController.asStateFlow()


    private val _currentMusic: MutableStateFlow<MusicDto?> = MutableStateFlow(null)
    val currentMusic = _currentMusic.asStateFlow()

    fun addListenerToMediaController() {
        _mediaController.value?.addListener(object : Player.Listener {
            @OptIn(UnstableApi::class)
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItem?.let {
                    _currentMusic.update {

                        with(mediaItem) {
                            MusicDto(
                                mediaId.toLong(),
                                mediaMetadata.displayTitle.toString(),
                                mediaMetadata.artist.toString(),
                                mediaMetadata.durationMs ?: 0L,
                                mediaMetadata.artworkUri.toString(),
                                localConfiguration?.uri.toString()
                            )
                        }
                    }
                }
            }
        })
    }


    fun addListenerToMediaControllerFuture(
        onSuccess: () -> Unit,
    ) {
        mediaControllerFuture.addListener({
            try {
                _mediaController.update { mediaControllerFuture.get() }
                onSuccess()
                addListenerToMediaController()
            } catch (e: Exception) {
                _mediaController.update { null }
            }
        }, ContextCompat.getMainExecutor(context))
    }

    @OptIn(UnstableApi::class)
    fun setMediaItems(musicList: List<MusicDto>) {
        val mediaItems = musicList.map { music ->
            MediaItem.Builder()
                .setUri(music.filepath)
                .setMediaId(music.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setArtist(music.artist)
                        .setDisplayTitle(music.title)
                        .setDurationMs(music.duration)
                        .setArtworkUri(Uri.parse(music.image))
                        .build()
                )
                .build()
        }
        mediaController.value?.setMediaItems(mediaItems)
        mediaController.value?.prepare()
    }


    fun onMusicItemClick(index: Int) {
        when (index) {
            mediaController.value?.currentMediaItemIndex -> {
                playOrPause()
            }

            else -> {
                mediaController.value?.seekToDefaultPosition(index)
                mediaController.value?.prepare()
                mediaController.value?.playWhenReady = true

            }
        }
    }

    private fun playOrPause() {
        if (mediaController.value!!.isPlaying) {
            mediaController.value!!.pause()
        } else {
            mediaController.value!!.play()
        }
    }


}











