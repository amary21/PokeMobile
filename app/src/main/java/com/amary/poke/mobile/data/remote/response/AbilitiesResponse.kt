package com.amary.poke.mobile.data.remote.response

import com.amary.poke.mobile.domain.model.AbilitiesModel
import com.amary.poke.mobile.domain.model.AbilityModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AbilitiesResponse(
    @SerialName("ability")
    val ability: AbilityResponse?,
    @SerialName("is_hidden")
    val isHidden: Boolean?,
    @SerialName("slot")
    val slot: Int?
) {
    fun toDomain() = AbilitiesModel(
        ability = ability?.toDomain() ?: AbilityModel(),
        isHidden = isHidden ?: false,
        slot = slot ?: 0
    )
}