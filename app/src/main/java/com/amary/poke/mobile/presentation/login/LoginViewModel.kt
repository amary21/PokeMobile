package com.amary.poke.mobile.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amary.poke.mobile.domain.usecase.AuthUseCase
import com.amary.poke.mobile.domain.usecase.LoginUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events get() = _events.receiveAsFlow()

    init {
        checkAuthentication()
    }

    fun checkAuthentication() {
        viewModelScope.launch {
            authUseCase.invoke()
                .onSuccess { isAuthenticated ->
                    if (isAuthenticated) {
                        _events.send(LoginEvent.Success)
                    }
                }
                .onFailure { exception ->
                    _events.send(LoginEvent.Error(exception.message ?: "Authentication check failed"))
                }
        }
    }

    fun login(username: String, password: String) = viewModelScope.launch {
         _events.send(LoginEvent.Loading)
        if (username.isBlank() || password.isBlank()) {
            _events.send(LoginEvent.LoadingComplete)
            _events.send(LoginEvent.Error("Username and password cannot be empty"))
            return@launch
        }

        loginUseCase.invoke(username, password)
            .onSuccess {
                _events.send(LoginEvent.LoadingComplete)
                _events.send(LoginEvent.Success)
            }
            .onFailure { exception ->
                _events.send(LoginEvent.LoadingComplete)
                _events.send(LoginEvent.Error(exception.message ?: "Unknown error"))
            }
    }
}