package com.amary.poke.mobile.presentation.register

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

//TODO: Implement RegisterViewModel
class RegisterViewModel : ViewModel() {
    private val _state = MutableStateFlow<RegisterState>(RegisterState.Initial)
    val state = _state.asStateFlow()
}