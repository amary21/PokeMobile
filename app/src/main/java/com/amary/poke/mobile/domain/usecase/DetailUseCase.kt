package com.amary.poke.mobile.domain.usecase

import com.amary.poke.mobile.domain.model.DetailModel
import com.amary.poke.mobile.domain.repository.PokeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DetailUseCase(
    private val repository: PokeRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    operator fun invoke(name: String): Flow<Result<DetailModel>> = flow {
        try {
            val model = repository.getPokemonDetail(name)
            emit(Result.success(model))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(dispatcher)
}