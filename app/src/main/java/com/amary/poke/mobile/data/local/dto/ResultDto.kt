package com.amary.poke.mobile.data.local.dto

import com.amary.poke.mobile.domain.model.ResultModel

data class ResultDto(
    val name: String = "",
    val url: String = ""
) {
    fun toDomain() = ResultModel (
        name = name,
        url = url
    )

    companion object {

        fun fromDomain(model: ResultModel) = ResultDto(
            name = model.name,
            url = model.url
        )
    }
}