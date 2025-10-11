package com.amary.poke.mobile.presentation.register

sealed class RegisterEvent {
    data object Loading : RegisterEvent()
    data object LoadingComplete : RegisterEvent()
    data object Success : RegisterEvent()
    data class Error(val message: String) : RegisterEvent()
}