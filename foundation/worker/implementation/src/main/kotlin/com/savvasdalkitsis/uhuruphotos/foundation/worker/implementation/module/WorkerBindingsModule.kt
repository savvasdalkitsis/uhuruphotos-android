/*
Copyright 2023 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
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
