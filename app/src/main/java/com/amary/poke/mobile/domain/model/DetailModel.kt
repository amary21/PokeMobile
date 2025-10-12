package com.amary.poke.mobile.domain.model

data class DetailModel(
    val abilities: List<AbilitiesModel> = emptyList(),
    val baseExperience: Int = 0,
    val height: Int = 0,
    val id: Int = 0,
    val isDefault: Boolean = false,
    val locationAreaEncounters: String = "",
    val name: String = "",
    val order: Int = 0,
    val weight: Int = 0
)
