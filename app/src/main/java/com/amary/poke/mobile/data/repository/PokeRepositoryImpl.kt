package com.amary.poke.mobile.data.repository

import com.amary.poke.mobile.data.local.dto.AuthDto
import com.amary.poke.mobile.data.local.dto.ResultDto
import com.amary.poke.mobile.data.local.dto.UserDto
import com.amary.poke.mobile.data.local.source.LocalSource
import com.amary.poke.mobile.data.remote.api.PokeApi
import com.amary.poke.mobile.domain.model.AuthModel
import com.amary.poke.mobile.domain.model.DetailModel
import com.amary.poke.mobile.domain.model.PokeModel
import com.amary.poke.mobile.domain.model.ResultModel
import com.amary.poke.mobile.domain.model.UserModel
import com.amary.poke.mobile.domain.repository.PokeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class PokeRepositoryImpl(
    private val pokeApi: PokeApi,
    private val localSource: LocalSource,
    private val ioDispatcher: CoroutineDispatcher,
): PokeRepository {
    override suspend fun listPokemon(
        limit: Int,
        offset: Int
    ): PokeModel = withContext(ioDispatcher) {
        val response = pokeApi.getPokemon(limit, offset)
        val offset = response.next
            ?.substringAfter("?")
            ?.split("&")
            ?.firstOrNull { it.startsWith("offset=") }
            ?.substringAfter("=")
            ?.toIntOrNull() ?: 10

        response.toDomain(offset)
    }

    override suspend fun getPokemonDetail(name: String): DetailModel = withContext(ioDispatcher) {
        val response = pokeApi.getPokemonDetail(name)
        response.toDomain()
    }

    override suspend fun listLocalPokemon(): List<ResultModel> = withContext(ioDispatcher) {
        localSource.getAll().map { it.toDomain() }
    }

    override suspend fun savePokemon(pokemon: List<ResultModel>) = withContext(ioDispatcher) {
        localSource.insert(pokemon.map { ResultDto.fromDomain(it) })
    }

    override suspend fun deletePokemon() = withContext(ioDispatcher) {
        localSource.deleteAll()
    }

    override suspend fun isUsernameExists(username: String): Boolean = withContext(ioDispatcher) {
        localSource.isUsernameExists(username)
    }

    override suspend fun insertUser(user: UserModel) = withContext(ioDispatcher) {
        localSource.insertUser(UserDto.fromDomain(user))
    }

    override suspend fun login(
        username: String,
        password: String
    ): UserModel? = withContext(ioDispatcher) {
        localSource.login(username, password)?.toDomain()
    }

    override suspend fun getUserById(userId: Int): UserModel? = withContext(ioDispatcher) {
        localSource.getUserById(userId)?.toDomain()
    }

    override suspend fun insertAuth(auth: AuthModel) = withContext(ioDispatcher) {
        localSource.insertAuth(AuthDto.fromDomain(auth))
    }

    override suspend fun logout() = withContext(ioDispatcher) {
        localSource.logout()
    }

    override suspend fun isAuthenticated(): Boolean = withContext(ioDispatcher) {
        localSource.isAuthenticated()
    }

    override suspend fun getAuth(): AuthModel? = withContext(ioDispatcher) {
        localSource.getAuth()?.toDomain()
    }
}