package com.amary.poke.mobile.data.local.dto

import com.amary.poke.mobile.domain.model.AuthModel

data class AuthDto (
    val id: Int = 0,
) {
    fun toDomain() = AuthModel(
        id = id
    )

    fun toMap() = mapOf(
        "id" to id
    )

    companion object {
        fun fromMap(map: Map<String?, Any?>?) = AuthDto(
            id = (map?.get("id") as? Number)?.toInt() ?: 0
        )

        fun fromDomain(domain: AuthModel) = AuthDto(
            id = domain.id
        )
    }
}