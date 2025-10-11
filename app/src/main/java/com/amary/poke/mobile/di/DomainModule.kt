package com.amary.poke.mobile.di

import com.amary.poke.mobile.domain.usecase.AuthUseCase
import com.amary.poke.mobile.domain.usecase.LoginUseCase
import com.amary.poke.mobile.domain.usecase.RegisterUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { AuthUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
}