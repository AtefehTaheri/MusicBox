package ir.atefehtaheri.musicbox.feature.musicplayer

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.atefehtaheri.musicbox.data.musiclist.local.models.MusicDto
import javax.inject.Inject

class PlayerHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaControllerFuture: ListenableFuture<MediaController>,
) {



    private var mediaController: MediaController? = null


    fun addListenerToMediaController(
        onSuccess: () -> Unit,
    ) {
        mediaControllerFuture.addListener({
            try {
                mediaController = mediaControllerFuture.get()
                onSuccess()

            } catch (e: Exception) {
                mediaController = null
            }
        }, ContextCompat.getMainExecutor(context))
    }

    @OptIn(UnstableApi::class)
    fun setMediaItems(musicList: List<MusicDto>) {
        val mediaItems = musicList.map { music ->
            MediaItem.Builder()
                .setUri(music.filepath)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(music.title)
                        .setAlbumTitle(music.title)
                        .setAlbumArtist(music.artist)
                        .setDisplayTitle(music.title)
                        .setDurationMs(music.duration)
                        .setArtworkUri(Uri.parse(music.image))
                        .build()
                )
                .build()
        }
        mediaController?.setMediaItems(mediaItems)
        mediaController?.prepare()
        mediaController?.playWhenReady = false
    }


    fun onMusicItemClick(index: Int) {
        when (index) {
            mediaController?.currentMediaItemIndex -> {
                playOrPause()
            }

            else -> {
                mediaController?.seekToDefaultPosition(index)
                mediaController?.prepare()
                mediaController?.playWhenReady = true

            }
        }
    }

    private fun playOrPause() {
        if (mediaController!!.isPlaying) {
            mediaController!!.pause()
        } else {
            mediaController!!.play()
        }
    }


}











