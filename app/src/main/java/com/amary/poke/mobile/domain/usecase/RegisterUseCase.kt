package com.amary.poke.mobile.domain.usecase

import android.util.Log
import com.amary.poke.mobile.domain.model.UserModel
import com.amary.poke.mobile.domain.repository.PokeRepository


class RegisterUseCase(
    private val repository: PokeRepository
) {
    suspend operator fun invoke(
        username: String,
        fullName: String,
        email: String,
        password: String,
    ): Result<String> {
        try {
            var checkUser = repository.isUsernameExists(username)
            Log.e("invoke", checkUser.toString())
            if (checkUser) {
                return Result.failure(Exception("Username already exists"))
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
                if (!checkUser) return Result.failure(Exception("Registration failed"))
                return Result.success("Registration successful")
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}