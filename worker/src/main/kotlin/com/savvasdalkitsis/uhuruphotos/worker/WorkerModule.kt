package com.savvasdalkitsis.uhuruphotos.worker

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class WorkerModule {

    @Provides
    fun workManager(@ApplicationContext context: Context): WorkManager = WorkManager
        .getInstance(context)
}