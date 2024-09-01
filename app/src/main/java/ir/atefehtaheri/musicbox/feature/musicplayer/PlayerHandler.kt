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


    val currentMediaItem: MediaItem?
        get() = _mediaController.value?.currentMediaItem

    val currentPosition: Long?
        get() = _mediaController.value?.currentPosition


    fun addListenerToMediaController() {
        _mediaController.value?.addListener(object : Player.Listener {
            @OptIn(UnstableApi::class)
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItem?.let {
                    _currentMusic.update {
                        mediaItem.asMusicDto()
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
            music.asMediaItem()
        }
        mediaController.value?.setMediaItems(mediaItems)
        mediaController.value?.prepare()
    }

    fun seekTo(currentIndex: Int, currentPosition: Long) {
        mediaController.value?.seekTo(currentIndex, currentPosition)
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

    fun onDestroy() {
        MediaController.releaseFuture(mediaControllerFuture)
    }

}

@OptIn(UnstableApi::class)
fun MediaItem.asMusicDto(): MusicDto {
    return MusicDto(
        mediaId.toLong(),
        mediaMetadata.displayTitle.toString(),
        mediaMetadata.artist.toString(),
        mediaMetadata.durationMs ?: 0L,
        mediaMetadata.artworkUri.toString(),
        localConfiguration?.uri.toString()
    )
}

@OptIn(UnstableApi::class)
fun MusicDto.asMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setUri(this.filepath)
        .setMediaId(this.id.toString())
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setArtist(this.artist)
                .setDisplayTitle(this.title)
                .setDurationMs(this.duration)
                .setArtworkUri(Uri.parse(this.image))
                .build()
        )
        .build()
}










