package ir.atefehtaheri.musicbox.feature.musicplayer.model

import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import ir.atefehtaheri.musicbox.data.musiclist.local.models.MusicDto


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

