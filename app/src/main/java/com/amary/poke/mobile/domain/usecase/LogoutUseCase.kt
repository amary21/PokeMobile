package com.amary.poke.mobile.domain.usecase

import com.amary.poke.mobile.domain.repository.PokeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LogoutUseCase(
    private val repository: PokeRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(): Result<String> = withContext(dispatcher) {
        try {
            repository.logout()
            Result.success("Logout successful")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}