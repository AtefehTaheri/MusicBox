package ir.atefehtaheri.musicbox.data.musiclist.local

import ir.atefehtaheri.musicbox.core.common.models.ResultStatus
import ir.atefehtaheri.musicbox.data.musiclist.local.models.MusicDto

interface MusicListDataSource {

    suspend fun getLocalMusics(): ResultStatus<List<MusicDto>>
}