package com.amary.poke.mobile.di

import com.amary.poke.mobile.coroutine.Dispatcher
import com.amary.poke.mobile.domain.usecase.AuthUseCase
import com.amary.poke.mobile.domain.usecase.DetailUseCase
import com.amary.poke.mobile.domain.usecase.ListUseCase
import com.amary.poke.mobile.domain.usecase.LoginUseCase
import com.amary.poke.mobile.domain.usecase.LogoutUseCase
import com.amary.poke.mobile.domain.usecase.ProfileUseCase
import com.amary.poke.mobile.domain.usecase.RegisterUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module

val domainModule = module {
    factory { AuthUseCase(get(), get(named(Dispatcher.IO))) }
    factory { LoginUseCase(get(), get(named(Dispatcher.IO))) }
    factory { RegisterUseCase(get(), get(named(Dispatcher.IO))) }
    factory { ListUseCase(get(), get(named(Dispatcher.IO))) }
    factory { DetailUseCase(get(), get(named(Dispatcher.IO))) }
    factory { ProfileUseCase(get(), get(named(Dispatcher.IO))) }
    factory { LogoutUseCase(get(), get(named(Dispatcher.IO))) }
}