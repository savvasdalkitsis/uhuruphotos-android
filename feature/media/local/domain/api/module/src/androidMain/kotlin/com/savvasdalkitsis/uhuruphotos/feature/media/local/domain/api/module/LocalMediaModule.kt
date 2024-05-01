/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.module

import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaDeletionUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.initializer.LocalMediaInitializer
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.module.LocalMediaModule
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance

object LocalMediaModule {

    val localMediaUseCase: LocalMediaUseCase
        get() = LocalMediaModule.localMediaUseCase

    val localMediaWorkScheduler: LocalMediaWorkScheduler
        get() = LocalMediaModule.localMediaWorkScheduler

    val localMediaInitializer: LocalMediaInitializer by singleInstance {
        LocalMediaInitializer(
            localMediaWorkScheduler,
            AndroidModule.contentResolver,
        )
    }

    val localMediaDeletionUseCase: LocalMediaDeletionUseCase by singleInstance {
        com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.usecase.LocalMediaDeletionUseCase(
            AndroidModule.applicationContext,
            LocalMediaModule.localMediaRepository,
            LocalMediaModule.localMediaService,
            AndroidModule.contentResolver,
            AndroidModule.activityRequestLauncher,
        )
    }
}