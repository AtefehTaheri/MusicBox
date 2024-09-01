package ir.atefehtaheri.musicbox.feature.musicplayer.di

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.atefehtaheri.musicbox.feature.musicplayer.MusicService
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MediaControllerModule {

//    @Singleton
    @Provides
    fun getMediaControllerModule(
        @ApplicationContext context: Context,
    ): ListenableFuture<MediaController> {

        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        return MediaController.Builder(context, sessionToken).buildAsync()
    }

}