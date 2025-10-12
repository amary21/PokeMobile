package com.amary.poke.mobile.data.remote.response

import androidx.compose.ui.geometry.Offset
import com.amary.poke.mobile.domain.model.PokeModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokeResponse(
    @SerialName("count")
    val count: Int? = null,
    @SerialName("next")
    val next: String? = null,
    @SerialName("previous")
    val previous: String? = null,
    @SerialName("results")
    val result: List<ResultResponse>? = null
) {
    fun toDomain(offset: Int) = PokeModel(
        next = offset,
        result = result?.map { it.toDomain() } ?: emptyList()
    )
}