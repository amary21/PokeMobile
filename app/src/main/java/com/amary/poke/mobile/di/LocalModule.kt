package com.amary.poke.mobile.di

import com.amary.poke.mobile.coroutine.Dispatcher
import com.amary.poke.mobile.data.local.collection.DbCollection
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
    single<Collection>(named(DbCollection.USER)) {
        get<Database>().createCollection(DbCollection.USER.collection)
    }
    single<Collection>(named(DbCollection.AUTH)) {
        get<Database>().createCollection(DbCollection.AUTH.collection)
    }
    single<Collection>(named(DbCollection.POKE)) {
        get<Database>().createCollection(DbCollection.POKE.collection)
    }
    single<LocalSource> {
        LocalSourceImpl(
            get(named(DbCollection.AUTH)),
            get(named(DbCollection.USER)),
            get(named(DbCollection.POKE)),
            get(named(Dispatcher.IO))
        )
    }
}