package ir.atefehtaheri.musicbox.data.musiclist.repository

import ir.atefehtaheri.musicbox.core.common.models.ResultStatus
import ir.atefehtaheri.musicbox.data.musiclist.local.models.MusicDto
import kotlinx.coroutines.flow.Flow

interface MusicListRepository {

    fun getLocalMusicsFlow(): Flow<ResultStatus<List<MusicDto>>>
}