package com.savvasdalkitsis.uhuruphotos.foundation.log.implementation.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import timber.log.ConsoleTree
import timber.log.Timber

@Module
@InstallIn(SingletonComponent::class)
internal class LogExtraModule {

    @Provides
    @IntoSet
    fun consoleTree(): Timber.Tree = ConsoleTree()
}