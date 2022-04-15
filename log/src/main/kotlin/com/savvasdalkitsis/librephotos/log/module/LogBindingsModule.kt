package com.savvasdalkitsis.librephotos.log.module

import com.savvasdalkitsis.librephotos.initializer.ApplicationCreated
import com.savvasdalkitsis.librephotos.log.initializer.LogInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class LogBindingsModule {

    @Binds
    @IntoSet
    abstract fun logInitializer(logInitializer: LogInitializer): ApplicationCreated
}