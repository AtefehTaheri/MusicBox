package ir.atefehtaheri.musicbox.feature.musiclist

import android.content.Context
import android.content.IntentSender
import android.net.Uri
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.atefehtaheri.musicbox.core.common.models.ResultStatus
import ir.atefehtaheri.musicbox.data.musiclist.repository.MusicListRepository
import ir.atefehtaheri.musicbox.feature.musiclist.uistate.MusicListUiState
import ir.atefehtaheri.musicbox.feature.musicplayer.PlayerHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class MusicListViewModel @Inject constructor(
    private val musicListRepository: MusicListRepository,
    private val playerHandler: PlayerHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicListUiState())
    val uiState = _uiState.asStateFlow()


    init {
        playerHandler.addListenerToMediaController(
            onSuccess = ::loadData
        )
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
                        }
                    }
                    setMediaItems()
                }
        }
    }

    private fun setMediaItems() {
        playerHandler.setMediaItems(_uiState.value.musicList)
    }


    fun onMusicItemClick(index: Int) {
        playerHandler.onMusicItemClick(index)
        _uiState.update {
            it.copy(
                currentMusic = it.musicList.get(index)
            )
        }
    }


    fun deleteMusic(idMusic: Long, handleException: (IntentSender) -> Unit) {
        musicListRepository.deleteMusic(idMusic, handleException)
    }

}


