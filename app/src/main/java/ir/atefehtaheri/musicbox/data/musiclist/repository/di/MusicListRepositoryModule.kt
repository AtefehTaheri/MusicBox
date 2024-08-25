package ir.atefehtaheri.musicbox.data.musiclist.repository.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.atefehtaheri.musicbox.data.musiclist.repository.LocalMusicListRepository
import ir.atefehtaheri.musicbox.data.musiclist.repository.MusicListRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface MusicListRepositoryModule {

    @Singleton
    @Binds
    fun getMusicListRepositoryModule(
        LocalMusicListRepository: LocalMusicListRepository
    ): MusicListRepository

}