package com.amary.poke.mobile.di

import com.amary.poke.mobile.data.local.source.LocalSource
import com.amary.poke.mobile.data.local.source.LocalSourceImpl
import com.couchbase.lite.Collection
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.Database
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val localModule = module {
    single<Database> {
        CouchbaseLite.init(androidContext())
        Database("poke.db")
    }
    single<Collection>(qualifier = named("user_collection")) {
        get<Database>().createCollection("user_collection")
    }
    single<Collection>(qualifier = named("auth_collection")) {
        get<Database>().createCollection("auth_collection")
    }
    single<LocalSource> {
        LocalSourceImpl(
            get(qualifier = named("auth_collection")),
            get(qualifier = named("user_collection"))
        )
    }
}