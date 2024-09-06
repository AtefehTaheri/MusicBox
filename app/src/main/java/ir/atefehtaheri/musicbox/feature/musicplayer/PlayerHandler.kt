package ir.atefehtaheri.musicbox.feature.musicplayer

import android.content.Context
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.atefehtaheri.musicbox.data.musiclist.local.models.MusicDto
import ir.atefehtaheri.musicbox.feature.musicplayer.model.asMediaItem
import ir.atefehtaheri.musicbox.feature.musicplayer.model.asMusicDto
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


    @OptIn(UnstableApi::class)
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


    fun addListenerToMediaControllerFuture() {
        mediaControllerFuture.addListener({
            try {
                _mediaController.update {
                    mediaControllerFuture.get()
                }
                addListenerToMediaController()
            } catch (e: Exception) {
                _mediaController.update { null }
            }
        },
            MoreExecutors.directExecutor()
        )
    }


    fun setMediaItems(musicList: List<MusicDto>) {

        val mediaItems = musicList.map { music ->
            music.asMediaItem()
        }
        _mediaController.value?.setMediaItems(mediaItems)
        _currentMusic.update {
            _mediaController.value?.currentMediaItem?.asMusicDto()
        }

    }

    fun seekTo(currentMusicDto:MusicDto?,currentIndex: Int, currentPosition: Long) {
        _mediaController.value?.seekTo(currentIndex, currentPosition)
        _currentMusic.update {
            currentMusicDto
        }
    }


    fun onMusicItemClick(index: Int) {

        when (index) {
            _mediaController.value?.currentMediaItemIndex -> {
                playOrPause()
            }

            else -> {
                _mediaController.value?.seekToDefaultPosition(index)
                _mediaController.value?.prepare()
                _mediaController.value?.playWhenReady = true
            }
        }
    }


    private fun playOrPause() {
        if (_mediaController.value!!.isPlaying) {
            _mediaController.value!!.pause()
        } else {
            _mediaController.value!!.play()
        }
    }

    fun onDestroy() {
        MediaController.releaseFuture(mediaControllerFuture)
    }

}









