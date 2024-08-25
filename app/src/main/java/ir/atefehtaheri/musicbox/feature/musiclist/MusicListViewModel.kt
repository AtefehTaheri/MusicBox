package ir.atefehtaheri.musicbox.feature.musiclist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.atefehtaheri.musicbox.core.common.models.ResultStatus
import ir.atefehtaheri.musicbox.data.musiclist.repository.MusicListRepository
import ir.atefehtaheri.musicbox.feature.musiclist.uistate.MusicListUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class MusicListViewModel @Inject constructor(
    private val musicListRepository: MusicListRepository,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = musicListRepository.getLocalMusicsFlow()
        .mapLatest { result ->
            when (result) {
                is ResultStatus.Failure -> MusicListUiState(
                    isLoading = false,
                    errorMessage = result.exception_message,
                    musicList = emptyList()
                )

                is ResultStatus.Success -> MusicListUiState(
                    isLoading = false,
                    errorMessage = null,
                    musicList = result.data ?: emptyList()
                )
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MusicListUiState()
        )
}


