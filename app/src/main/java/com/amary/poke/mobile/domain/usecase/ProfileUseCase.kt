package com.amary.poke.mobile.domain.usecase

import com.amary.poke.mobile.domain.model.UserModel
import com.amary.poke.mobile.domain.repository.PokeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ProfileUseCase(
    private val repository: PokeRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    operator fun invoke(): Flow<Result<UserModel>> = flow {
        try {
            val auth = repository.getAuth()
            auth?.let {
                val user = repository.getUserById(it.id)
                user?.let {
                    emit(Result.success(it))
                } ?: run {
                    emit(Result.failure(Exception("User not found")))
                }
            } ?: run {
                emit(Result.failure(Exception("Auth not found")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(dispatcher)
}