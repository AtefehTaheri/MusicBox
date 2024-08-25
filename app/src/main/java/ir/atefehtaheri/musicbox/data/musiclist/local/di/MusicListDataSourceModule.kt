package ir.atefehtaheri.musicbox.data.musiclist.local.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.atefehtaheri.musicbox.data.musiclist.local.LocalMusicListDataSource
import ir.atefehtaheri.musicbox.data.musiclist.local.MusicListDataSource
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface MusicListDataSourceModule {

    @Singleton
    @Binds
    fun getMusicListDataSourceModule(
        localMusicListDataSource: LocalMusicListDataSource
    ): MusicListDataSource

}