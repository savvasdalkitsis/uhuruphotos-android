package com.savvasdalkitsis.librephotos.log.module

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.LogAdapter
import com.orhanobut.logger.PrettyFormatStrategy
import com.savvasdalkitsis.librephotos.log.BuildConfig
import com.savvasdalkitsis.librephotos.log.NoOpLogAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class LogModule {

    @Provides
    fun logAdapter(): LogAdapter = when  {
        BuildConfig.DEBUG -> AndroidLogAdapter(
            PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)
                .methodCount(0)
                .tag("")
                .build())
        else -> NoOpLogAdapter()
    }
}