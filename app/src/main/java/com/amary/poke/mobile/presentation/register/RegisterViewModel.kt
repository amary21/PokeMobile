package com.amary.poke.mobile.presentation.register

import android.util.Patterns.EMAIL_ADDRESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amary.poke.mobile.domain.usecase.RegisterUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _events = Channel<RegisterEvent>(Channel.BUFFERED)
    val events get() = _events.receiveAsFlow()

    fun register(
        username: String,
        fullName: String,
        email: String,
        password: String
    ) = viewModelScope.launch {
        _events.send(RegisterEvent.Loading)

        if (username.isBlank() || fullName.isBlank() || email.isBlank() || password.isBlank()) {
            _events.send(RegisterEvent.LoadingComplete)
            _events.send(RegisterEvent.Error("All fields are required"))
            return@launch
        }

        if (!isValidEmail(email)) {
            _events.send(RegisterEvent.LoadingComplete)
            _events.send(RegisterEvent.Error("Invalid email format"))
            return@launch
        }

        registerUseCase.invoke(username, fullName, email, password)
            .onSuccess {
                _events.send(RegisterEvent.LoadingComplete)
                _events.send(RegisterEvent.Success)
            }
            .onFailure { exception ->
                _events.send(RegisterEvent.LoadingComplete)
                _events.send(RegisterEvent.Error(exception.message ?: "Unknown error"))
            }
    }

    private fun isValidEmail(email: String): Boolean {
        return EMAIL_ADDRESS.matcher(email).matches()
    }
}