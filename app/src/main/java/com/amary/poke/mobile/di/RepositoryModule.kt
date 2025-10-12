package com.amary.poke.mobile.di

import com.amary.poke.mobile.coroutine.Dispatcher
import com.amary.poke.mobile.data.repository.PokeRepositoryImpl
import com.amary.poke.mobile.domain.repository.PokeRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<PokeRepository> {
        PokeRepositoryImpl(
            pokeApi = get(),
            localSource = get(),
            ioDispatcher = get(named(Dispatcher.IO))
        )
    }
}