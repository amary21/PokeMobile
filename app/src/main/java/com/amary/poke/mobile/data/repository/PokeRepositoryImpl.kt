package com.amary.poke.mobile.data.repository

import com.amary.poke.mobile.data.local.dto.AuthDto
import com.amary.poke.mobile.data.local.dto.UserDto
import com.amary.poke.mobile.data.local.source.LocalSource
import com.amary.poke.mobile.domain.model.AuthModel
import com.amary.poke.mobile.domain.model.UserModel
import com.amary.poke.mobile.domain.repository.PokeRepository

class PokeRepositoryImpl(
    private val localSource: LocalSource
): PokeRepository {
    override suspend fun isUsernameExists(username: String): Boolean {
        return localSource.isUsernameExists(username)
    }

    override suspend fun insertUser(user: UserModel) {
        localSource.insertUser(UserDto.fromDomain(user))
    }

    override suspend fun login(
        username: String,
        password: String
    ): UserModel? {
        return localSource.login(username, password)?.toDomain()
    }

    override suspend fun insertAuth(auth: AuthModel) {
        localSource.insertAuth(AuthDto.fromDomain(auth))
    }

    override suspend fun logout() {
        localSource.logout()
    }

    override suspend fun isAuthenticated(): Boolean {
        return localSource.isAuthenticated()
    }
}