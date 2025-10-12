package com.amary.poke.mobile.data.remote.response

import com.amary.poke.mobile.domain.model.AbilityModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AbilityResponse(
    @SerialName("name")
    val name: String?,
    @SerialName("url")
    val url: String?
) {
    fun toDomain() = AbilityModel(
        name = name ?: "",
        url = url ?: ""
    )
}