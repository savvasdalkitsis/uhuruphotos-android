package com.savvasdalkitsis.uhuruphotos.foundation.worker.implementation.module

import com.savvasdalkitsis.uhuruphotos.foundation.worker.implementation.usecase.WorkScheduleUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.worker.implementation.usecase.WorkerStatusUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerBindingsModule {

    @Binds
    abstract fun workScheduler(scheduler: WorkScheduleUseCase):
            com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkScheduleUseCase

    @Binds
    abstract fun workerStatusUseCase(useCase: WorkerStatusUseCase):
            com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkerStatusUseCase
}