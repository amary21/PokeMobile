package com.amary.poke.mobile.di

import com.amary.poke.mobile.coroutine.Dispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coroutineModule = module {
    single(named(Dispatcher.IO)) { Dispatchers.IO }
    single(named(Dispatcher.MAIN)) { Dispatchers.Main }
    single(named(Dispatcher.DEFAULT)) { Dispatchers.Default }
    single(named(Dispatcher.UNCONFINED)) { Dispatchers.Unconfined }
}