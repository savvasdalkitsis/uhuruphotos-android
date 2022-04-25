package com.savvasdalkitsis.uhuruphotos.log.module

import com.savvasdalkitsis.uhuruphotos.log.BuildConfig
import com.savvasdalkitsis.uhuruphotos.log.NoOpTree
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import timber.log.Timber

@Module
@InstallIn(SingletonComponent::class)
internal class LogModule {

    @Provides
    fun tree(): Timber.Tree = when  {
        BuildConfig.DEBUG -> Timber.DebugTree()
        else -> NoOpTree()
    }
}