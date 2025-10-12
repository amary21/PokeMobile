package com.amary.poke.mobile.presentation.detail

import com.amary.poke.mobile.domain.model.DetailModel

sealed class DetailState {
    data object Initial: DetailState()
    object Loading: DetailState()
    data class Success(val data: DetailModel): DetailState()
    data class Error(val message: String): DetailState()
}