package com.amary.poke.mobile.domain.usecase

import com.amary.poke.mobile.domain.repository.PokeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class AuthUseCase(
    private val repository: PokeRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(): Result<Boolean> = withContext(dispatcher) {
        try {
            Result.success(repository.isAuthenticated())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}