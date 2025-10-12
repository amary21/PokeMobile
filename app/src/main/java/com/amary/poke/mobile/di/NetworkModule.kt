package com.amary.poke.mobile.di

import com.amary.poke.mobile.data.remote.api.PokeApi
import com.chuckerteam.chucker.api.ChuckerInterceptor
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .cache(null)
            .addInterceptor(ChuckerInterceptor(androidContext()))
            .build()
    }
    single {
        val networkJson = Json { ignoreUnknownKeys = true }
        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(
                networkJson
                    .asConverterFactory("application/json".toMediaType())
            )
            .client(get())
            .build()
    }
    single<PokeApi> {
        get<Retrofit>().create(PokeApi::class.java)
    }
}