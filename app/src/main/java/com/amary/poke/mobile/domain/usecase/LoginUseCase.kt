package com.amary.poke.mobile.domain.usecase

import com.amary.poke.mobile.domain.model.AuthModel
import com.amary.poke.mobile.domain.repository.PokeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LoginUseCase(
    private val repository: PokeRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        username: String,
        password: String
    ): Result<String> = withContext(dispatcher) {
        try {
            val user = repository.login(username, password)
            user?.let {
                repository.insertAuth(AuthModel(id = it.id))
                Result.success("Login successful")
            } ?: run {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}