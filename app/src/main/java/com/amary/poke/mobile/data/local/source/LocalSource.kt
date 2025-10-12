package com.amary.poke.mobile.data.local.source

import com.amary.poke.mobile.data.local.dto.AuthDto
import com.amary.poke.mobile.data.local.dto.ResultDto
import com.amary.poke.mobile.data.local.dto.UserDto

interface LocalSource {
    suspend fun getAll(): List<ResultDto>
    suspend fun deleteAll()
    suspend fun insert(results: List<ResultDto>)
    suspend fun isUsernameExists(username: String): Boolean
    suspend fun insertUser(user: UserDto)
    suspend fun getUserById(userId: Int): UserDto?
    suspend fun login(username: String, password: String): UserDto?
    suspend fun insertAuth(auth: AuthDto)
    suspend fun logout()
    suspend fun isAuthenticated(): Boolean
    suspend fun getAuth(): AuthDto?
}