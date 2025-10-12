package com.amary.poke.mobile.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amary.poke.mobile.domain.usecase.AuthUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _events = Channel<SplashEvent>(Channel.BUFFERED)
    val events get() = _events.receiveAsFlow()

    init {
        checkAuthentication()
    }

    fun checkAuthentication() {
        viewModelScope.launch {
            authUseCase.invoke()
                .onSuccess { isAuthenticated ->
                    if (isAuthenticated) {
                        _events.send(SplashEvent.Success)
                    }
                }
                .onFailure { exception ->
                    _events.send(SplashEvent.Error(exception.message ?: "Authentication check failed"))
                }
        }
    }
}