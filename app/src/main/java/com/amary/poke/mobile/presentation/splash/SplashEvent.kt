package com.amary.poke.mobile.presentation.splash

sealed class SplashEvent {
    data object Loading : SplashEvent()
    data object LoadingComplete : SplashEvent()
    data object Success : SplashEvent()
    data class Error(val message: String) : SplashEvent()
}