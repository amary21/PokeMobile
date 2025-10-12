package com.amary.poke.mobile.data.remote.api

import com.amary.poke.mobile.data.remote.response.DetailResponse
import com.amary.poke.mobile.data.remote.response.PokeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApi {
    @GET("pokemon")
    suspend fun getPokemon(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokeResponse

    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(
        @Path("name") name: String
    ): DetailResponse
}