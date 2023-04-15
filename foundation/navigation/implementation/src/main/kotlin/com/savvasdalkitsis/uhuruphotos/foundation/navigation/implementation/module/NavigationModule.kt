package com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.serialization.SealedClassTypeAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class NavigationModule {

    @Provides
    fun gson(): Gson = GsonBuilder()
        .registerTypeAdapterFactory(SealedClassTypeAdapterFactory())
        .create()
}