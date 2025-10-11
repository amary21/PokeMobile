package com.amary.poke.mobile.di

import com.amary.poke.mobile.data.repository.PokeRepositoryImpl
import com.amary.poke.mobile.domain.repository.PokeRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<PokeRepository> { PokeRepositoryImpl(get()) }
}