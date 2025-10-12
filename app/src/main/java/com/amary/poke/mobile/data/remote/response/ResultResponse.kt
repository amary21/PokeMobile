package com.amary.poke.mobile.data.remote.response

import com.amary.poke.mobile.domain.model.ResultModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResultResponse(
    @SerialName("name")
    val name: String? = null,
    @SerialName("url")
    val url: String? = null
) {
    fun toDomain() = ResultModel(
        name = name ?: "",
        url = url ?: ""
    )
}