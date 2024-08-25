package ir.atefehtaheri.musicbox.data.musiclist.repository

import ir.atefehtaheri.musicbox.data.musiclist.local.MusicListDataSource
import javax.inject.Inject

class LocalMusicListRepository @Inject constructor(
    private val musicListDataSource: MusicListDataSource,
) : MusicListRepository {

    override fun getLocalMusicsFlow() = musicListDataSource.getLocalMusicsFlow()

}
