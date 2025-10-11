package com.amary.poke.mobile.presentation.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

//TODO: Implement LoginViewModel
class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow<LoginState>(LoginState.Initial)
    val state = _state.asStateFlow()
}