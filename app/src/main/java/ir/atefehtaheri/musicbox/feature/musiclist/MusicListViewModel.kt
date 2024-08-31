package ir.atefehtaheri.musicbox.feature.musiclist

import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.atefehtaheri.musicbox.core.common.models.ResultStatus
import ir.atefehtaheri.musicbox.data.musiclist.repository.MusicListRepository
import ir.atefehtaheri.musicbox.feature.musiclist.uistate.MusicListUiState
import ir.atefehtaheri.musicbox.feature.musicplayer.PlayerHandler
import ir.atefehtaheri.musicbox.feature.musicplayer.asMusicDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MusicListViewModel @Inject constructor(
    private val musicListRepository: MusicListRepository,
    private val playerHandler: PlayerHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicListUiState())
    val uiState = _uiState.asStateFlow()

    val mediaController = playerHandler.mediaController

    init {
        playerHandler.addListenerToMediaControllerFuture(
            onSuccess = ::loadData
        )
        viewModelScope.launch {
            playerHandler.currentMusic.collectLatest { currentMusic ->
                _uiState.update {
                    it.copy(currentMusic = currentMusic)
                }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            musicListRepository.getLocalMusicsFlow()
                .collectLatest { result ->
                    when (result) {
                        is ResultStatus.Failure ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = result.exception_message,
                                    musicList = emptyList()
                                )
                            }

                        is ResultStatus.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = null,
                                    musicList = result.data ?: emptyList()
                                )
                            }
                            setMediaItems()
                        }
                    }
                }
        }
    }

    private fun setMediaItems() {

        val currentPosition = playerHandler.currentPosition
        val currentMusicDto = playerHandler.currentMediaItem?.asMusicDto()
        val index = _uiState.value.musicList.indexOf(currentMusicDto)
        playerHandler.setMediaItems(_uiState.value.musicList)
        if (index != -1){
            playerHandler.seekTo(index,currentPosition!!)
        }
    }


    fun onMusicItemClick(index: Int) {
        playerHandler.onMusicItemClick(index)
        _uiState.update {
            it.copy(
                currentMusic = it.musicList.get(index)
            )
        }
    }


    fun deleteMusicFile(idMusic: Long, handleException: (IntentSender) -> Unit) {
        musicListRepository.deleteMusic(idMusic, handleException)
    }


//    override fun onCleared() {
//        super.onCleared()
//        mediaController.value?.release()
//    }

}


