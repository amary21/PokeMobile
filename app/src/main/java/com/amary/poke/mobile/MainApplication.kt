package com.amary.poke.mobile

import android.app.Application
import com.amary.poke.mobile.di.coroutineModule
import com.amary.poke.mobile.di.domainModule
import com.amary.poke.mobile.di.localModule
import com.amary.poke.mobile.di.networkModule
import com.amary.poke.mobile.di.repositoryModule
import com.amary.poke.mobile.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MainApplication)
            modules(
                coroutineModule,
                localModule,
                networkModule,
                repositoryModule,
                domainModule,
                viewModelModule,
            )
        }
    }
}