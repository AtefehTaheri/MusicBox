package ir.atefehtaheri.musicbox.feature.musiclist.uistate

import ir.atefehtaheri.musicbox.data.musiclist.local.models.MusicDto

data class MusicListUiState(
    val isLoading: Boolean = true,
    val musicList: List<MusicDto> = emptyList(),
    val errorMessage: String? = null,
)
