package com.savvasdalkitsis.uhuruphotos.log.module

import com.savvasdalkitsis.uhuruphotos.initializer.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.log.initializer.LogInitializer
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