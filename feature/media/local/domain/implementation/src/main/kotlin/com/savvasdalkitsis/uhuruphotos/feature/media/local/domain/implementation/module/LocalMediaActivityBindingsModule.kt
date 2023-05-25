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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.initializer.LocalMediaInitializer
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.usecase.LocalMediaDeletionUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ActivityCreated
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
internal abstract class LocalMediaActivityBindingsModule {

    @Binds
    abstract fun localMediaDeletionUseCase(useCase: LocalMediaDeletionUseCase):
            com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaDeletionUseCase

    @Binds
    @IntoSet
    abstract fun localMediaInitializer(localMediaInitializer: LocalMediaInitializer):
            ActivityCreated

    @Binds
    abstract fun localMediaWorkScheduler(localMediaWorkScheduler: LocalMediaWorkScheduler):
            com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.worker.LocalMediaWorkScheduler
}
