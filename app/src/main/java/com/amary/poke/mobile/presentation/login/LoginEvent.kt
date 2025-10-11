package com.amary.poke.mobile.presentation.login

sealed class LoginEvent {
    data object Loading : LoginEvent()
    data object LoadingComplete : LoginEvent()
    data object Success : LoginEvent()
    data class Error(val message: String) : LoginEvent()
}