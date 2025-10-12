package com.amary.poke.mobile.presentation.profile

sealed class ProfileEvent {
    data object Loading : ProfileEvent()
    data object LoadingComplete : ProfileEvent()
    data object Success : ProfileEvent()
    data class Error(val message: String) : ProfileEvent()
}