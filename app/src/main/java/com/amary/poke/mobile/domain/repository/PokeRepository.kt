package com.amary.poke.mobile.domain.repository

import com.amary.poke.mobile.domain.model.AuthModel
import com.amary.poke.mobile.domain.model.UserModel

interface PokeRepository {

    suspend fun isUsernameExists(username: String): Boolean

    suspend fun insertUser(user: UserModel)

    suspend fun login(username: String, password: String): UserModel?

    suspend fun insertAuth(auth: AuthModel)

    suspend fun logout()

    suspend fun isAuthenticated(): Boolean
}