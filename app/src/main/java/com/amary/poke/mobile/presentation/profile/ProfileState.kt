package com.amary.poke.mobile.presentation.profile

import com.amary.poke.mobile.domain.model.UserModel

sealed class ProfileState {
    object Initial : ProfileState()
    object Loading : ProfileState()
    data class Success(val user: UserModel) : ProfileState()
    data class Error(val message: String) : ProfileState()
    object LoggedOut : ProfileState()
}