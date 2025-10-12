package com.amary.poke.mobile.domain.usecase

import com.amary.poke.mobile.domain.model.UserModel
import com.amary.poke.mobile.domain.repository.PokeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


class RegisterUseCase(
    private val repository: PokeRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        username: String,
        fullName: String,
        email: String,
        password: String,
    ): Result<String> = withContext(dispatcher) {
        try {
            var checkUser = repository.isUsernameExists(username)
            if (checkUser) {
                Result.failure(Exception("Username already exists"))
            } else {
                repository.insertUser(
                    user = UserModel(
                        userName = username,
                        fullName = fullName,
                        email = email,
                        password = password
                    )
                )

                checkUser = repository.isUsernameExists(username)
                if (!checkUser) {
                    Result.failure(Exception("Registration failed"))
                } else {
                    Result.success("Registration successful")
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}