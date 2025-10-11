package com.amary.poke.mobile.domain.usecase

import com.amary.poke.mobile.domain.model.AuthModel
import com.amary.poke.mobile.domain.repository.PokeRepository

class LoginUseCase(
    private val repository: PokeRepository
) {
    suspend operator fun invoke(
        username: String,
        password: String
    ): Result<String> {
        try {
            val user = repository.login(username, password)
            return user?.let {
                repository.insertAuth(AuthModel(id = it.id))
                Result.success("Login successful")
            } ?: run {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}