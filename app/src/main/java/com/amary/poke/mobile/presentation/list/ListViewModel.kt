package com.amary.poke.mobile.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amary.poke.mobile.domain.usecase.DetailUseCase
import com.amary.poke.mobile.domain.usecase.ListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListViewModel(
    private val listPokemonUseCase: ListUseCase,
    private val detailUseCase: DetailUseCase,
): ViewModel() {

    private val _listState = MutableStateFlow<ListState>(ListState.Loading)
    val listState = _listState.asStateFlow()

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchState = _searchState.asStateFlow()

    fun getList(offset: Int) = viewModelScope.launch {
        listPokemonUseCase.invoke(
            offset = offset
        ).collect { result ->
            _listState.value = ListState.Loading
            result.onSuccess {
                _listState.value = ListState.Success(it.result, it.next)
            }.onFailure { exception ->
                _listState.value = ListState.Error(exception.message.toString())
            }
        }
    }

    fun getSearch(name: String) {
        viewModelScope.launch {
            if (name.isNotEmpty()) {
                _searchState.value = SearchState.Loading
                detailUseCase.invoke(name).collect { result ->
                    result.onSuccess {
                        _searchState.value = SearchState.Success(it)
                    }.onFailure { exception ->
                        _searchState.value = SearchState.Error(exception.message.toString())
                    }
                }
            } else {
                _searchState.value = SearchState.Error("Name cannot be empty")
            }
        }
    }

}