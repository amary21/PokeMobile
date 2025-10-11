package com.amary.poke.mobile.domain.usecase

import com.amary.poke.mobile.domain.repository.PokeRepository

class AuthUseCase(
    private val repository: PokeRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        return try {
            Result.success(repository.isAuthenticated())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}