package com.amary.poke.mobile.domain.model

data class PokeModel(
    val next: Int = 0,
    val result: List<ResultModel> = emptyList()
)
