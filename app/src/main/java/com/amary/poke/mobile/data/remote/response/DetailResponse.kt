package com.amary.poke.mobile.data.remote.response

import com.amary.poke.mobile.domain.model.DetailModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetailResponse(
    @SerialName("abilities")
    val abilities: List<AbilitiesResponse>?,
    @SerialName("base_experience")
    val baseExperience: Int?,
    @SerialName("height")
    val height: Int?,
    @SerialName("id")
    val id: Int?,
    @SerialName("is_default")
    val isDefault: Boolean?,
    @SerialName("location_area_encounters")
    val locationAreaEncounters: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("order")
    val order: Int?,
    @SerialName("weight")
    val weight: Int?
) {
    fun toDomain() = DetailModel(
        abilities = abilities?.map { it.toDomain() } ?: emptyList(),
        baseExperience = baseExperience ?: 0,
        height = height ?: 0,
        id = id ?: 0,
        isDefault = isDefault ?: false,
        locationAreaEncounters = locationAreaEncounters ?: "",
        name = name ?: "",
        order = order ?: 0,
        weight = weight ?: 0
    )
}