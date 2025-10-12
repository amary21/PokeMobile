package com.amary.poke.mobile.data.local.dto

import com.amary.poke.mobile.domain.model.AuthModel

data class AuthDto (
    val id: String = "",
) {
    fun toDomain() = AuthModel(
        id = id
    )

    fun toMap() = mapOf(
        "id" to id
    )

    companion object {
        fun fromMap(map: Map<String?, Any?>?) = if (map == null) {
            null
        } else AuthDto(
            id = map["id"] as? String ?: ""
        )

        fun fromDomain(domain: AuthModel) = AuthDto(
            id = domain.id
        )
    }
}