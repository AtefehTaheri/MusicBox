package ir.atefehtaheri.musicbox.feature.musicshared

import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.atefehtaheri.musicbox.core.common.models.ResultStatus
import ir.atefehtaheri.musicbox.data.musiclist.local.models.MusicDto
import ir.atefehtaheri.musicbox.data.musiclist.repository.MusicListRepository
import ir.atefehtaheri.musicbox.feature.musicshared.uistate.MusicListUiState
import ir.atefehtaheri.musicbox.feature.musicplayer.PlayerHandler
import ir.atefehtaheri.musicbox.feature.musicplayer.model.asMusicDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class MusicListViewModel @Inject constructor(
    private val musicListRepository: MusicListRepository,
    private val playerHandler: PlayerHandler
) : ViewModel() {


    val mediaController = playerHandler.mediaController

    private val _uiState = MutableStateFlow(MusicListUiState())

    val uiState = combine(
        playerHandler.currentMusic,
        musicListRepository.getLocalMusicsFlow(),
        mediaController
    ) { currentMusic, musicListResult, mediaController ->
        mediaController?.let {
            when (musicListResult) {
                is ResultStatus.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = musicListResult.exception_message,
                            musicList = emptyList(),
                            currentMusic = null
                        )
                    }
                    _uiState.value
                }

                is ResultStatus.Success -> {
                    val musicList = musicListResult.data ?: emptyList()
                    if (_uiState.value.musicList != musicList) {
                        setMediaItems(musicList)
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null,
                            musicList = musicList,
                            currentMusic = currentMusic
                        )
                    }
                    _uiState.value
                }
            }
        } ?: MusicListUiState()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MusicListUiState()
    )


    init {
        playerHandler.addListenerToMediaControllerFuture()
    }


    private fun setMediaItems(musicList: List<MusicDto>) {

        val currentPosition = mediaController.value?.currentPosition
        val currentMusicDto = mediaController.value?.currentMediaItem?.asMusicDto()
        val index = musicList.indexOf(currentMusicDto)
        playerHandler.setMediaItems(musicList)
        if (index != -1) {
            playerHandler.seekTo(currentMusicDto,index, currentPosition!!)

        }
    }

    fun onMusicItemClick(index: Int) {
        playerHandler.onMusicItemClick(index)

    }

    fun deleteMusicFile(idMusic: Long, handleException: (IntentSender) -> Unit) {
        musicListRepository.deleteMusic(idMusic, handleException)
    }


    override fun onCleared() {
        super.onCleared()
//        mediaController?.value!!.release()
        playerHandler.onDestroy()
    }

}


