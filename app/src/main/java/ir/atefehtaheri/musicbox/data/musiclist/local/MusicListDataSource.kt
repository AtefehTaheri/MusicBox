package ir.atefehtaheri.musicbox.data.musiclist.local

import android.content.IntentSender
import ir.atefehtaheri.musicbox.core.common.models.ResultStatus
import ir.atefehtaheri.musicbox.data.musiclist.local.models.MusicDto
import kotlinx.coroutines.flow.Flow

interface MusicListDataSource {

     fun getLocalMusicsFlow(): Flow<ResultStatus<List<MusicDto>>>
     fun deleteMusic(idMusic:Long,handleException: (IntentSender) -> Unit)
}