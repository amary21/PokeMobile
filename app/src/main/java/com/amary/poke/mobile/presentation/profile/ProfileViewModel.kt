package com.amary.poke.mobile.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amary.poke.mobile.domain.usecase.LogoutUseCase
import com.amary.poke.mobile.domain.usecase.ProfileUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileUseCase: ProfileUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Initial)
    val state get() = _state.asStateFlow()

    private val _events = Channel<ProfileEvent>(Channel.BUFFERED)
    val events get() = _events.receiveAsFlow()

    fun getProfile() {
        viewModelScope.launch {
            _state.value = ProfileState.Loading
            profileUseCase.invoke().collect { result ->
                result.onSuccess { user ->
                    _state.value = ProfileState.Success(user)
                }.onFailure { exception ->
                    _state.value = ProfileState.Error(exception.message ?: "Unknown error")
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _events.send(ProfileEvent.Loading)
            logoutUseCase.invoke()
                .onSuccess {
                    _events.send(ProfileEvent.LoadingComplete)
                    _events.send(ProfileEvent.Success)
                }
                .onFailure { exception ->
                    _events.send(ProfileEvent.LoadingComplete)
                    _events.send(ProfileEvent.Error(exception.message ?: "Logout failed"))
                }
        }
    }
}
