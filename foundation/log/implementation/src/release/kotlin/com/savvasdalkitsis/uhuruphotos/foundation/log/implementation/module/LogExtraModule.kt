package com.savvasdalkitsis.uhuruphotos.foundation.log.implementation.module

import android.content.Context
import com.savvasdalkitsis.uhuruphotos.foundation.log.implementation.NoOpTree
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import timber.log.ConsoleTree
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class LogExtraModule {

    @Provides
    @IntoSet
    fun consoleTree(): Timber.Tree = NoOpTree()
}