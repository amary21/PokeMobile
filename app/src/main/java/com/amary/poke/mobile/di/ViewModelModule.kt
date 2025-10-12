package com.amary.poke.mobile.di

import com.amary.poke.mobile.presentation.detail.DetailViewModel
import com.amary.poke.mobile.presentation.list.ListViewModel
import com.amary.poke.mobile.presentation.login.LoginViewModel
import com.amary.poke.mobile.presentation.profile.ProfileViewModel
import com.amary.poke.mobile.presentation.register.RegisterViewModel
import com.amary.poke.mobile.presentation.splash.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { ListViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { DetailViewModel(get()) }
}
