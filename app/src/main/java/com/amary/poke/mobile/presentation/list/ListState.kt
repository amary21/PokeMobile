package com.amary.poke.mobile.presentation.list

import com.amary.poke.mobile.domain.model.DetailModel
import com.amary.poke.mobile.domain.model.ResultModel

sealed class ListState {
    data object Loading: ListState()
    data class Success(
        val list: List<ResultModel>,
        val offset: Int = 0,
    ): ListState()
    data class Error(val message: String): ListState()
}

sealed class SearchState {
    data object Initial: SearchState()
    data object Loading: SearchState()
    data class Success(val data: DetailModel): SearchState()
    data class Error(val message: String): SearchState()
}