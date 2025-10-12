package com.amary.poke.mobile.data.local.dto

import com.amary.poke.mobile.domain.model.UserModel

data class UserDto(
    val id: String = "",
    val userName: String = "",
    val fullName: String = "",
    val email: String = "",
    val password: String = ""
) {
    fun toDomain() = UserModel(
        id = id,
        userName = userName,
        fullName = fullName,
        email = email,
        password = password
    )

    fun toMap() = mapOf(
        "id" to id,
        "user_name" to userName,
        "full_name" to fullName,
        "email" to email,
        "password" to password
    )

    companion object {
        fun fromMap(map: Map<String?, Any?>?) = if (map == null) {
            null
        } else UserDto(
            id = map["id"] as? String ?: "",
            userName = map["user_name"] as? String ?: "",
            fullName = map["full_name"] as? String ?: "",
            email = map["email"] as? String ?: "",
            password = map["password"] as? String ?: ""
        )

        fun fromDomain(domain: UserModel) = UserDto(
            id = domain.id,
            userName = domain.userName,
            fullName = domain.fullName,
            email = domain.email,
            password = domain.password
        )
    }
}
