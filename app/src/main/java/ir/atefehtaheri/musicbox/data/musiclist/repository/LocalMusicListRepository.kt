package ir.atefehtaheri.musicbox.data.musiclist.repository

import android.content.IntentSender
import ir.atefehtaheri.musicbox.core.common.models.ResultStatus
import ir.atefehtaheri.musicbox.data.musiclist.local.MusicListDataSource
import javax.inject.Inject

class LocalMusicListRepository @Inject constructor(
    private val musicListDataSource: MusicListDataSource,
) : MusicListRepository {

    override fun getLocalMusicsFlow() = musicListDataSource.getLocalMusicsFlow()
    override fun deleteMusic(
        idMusic: Long,
        handleException: (IntentSender) -> Unit
    ) = musicListDataSource.deleteMusic(idMusic,handleException)

}
