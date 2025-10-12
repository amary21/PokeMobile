package com.amary.poke.mobile.domain.repository

import com.amary.poke.mobile.domain.model.AuthModel
import com.amary.poke.mobile.domain.model.DetailModel
import com.amary.poke.mobile.domain.model.PokeModel
import com.amary.poke.mobile.domain.model.ResultModel
import com.amary.poke.mobile.domain.model.UserModel

interface PokeRepository {

    suspend fun listPokemon(limit: Int, offset: Int): PokeModel

    suspend fun getPokemonDetail(name: String): DetailModel

    suspend fun listLocalPokemon(): List<ResultModel>

    suspend fun savePokemon(pokemon: List<ResultModel>)

    suspend fun deletePokemon()

    suspend fun isUsernameExists(username: String): Boolean

    suspend fun insertUser(user: UserModel)

    suspend fun login(username: String, password: String): UserModel?

    suspend fun getUserById(userId: Int): UserModel?

    suspend fun insertAuth(auth: AuthModel)

    suspend fun logout()

    suspend fun isAuthenticated(): Boolean

    suspend fun getAuth(): AuthModel?
}