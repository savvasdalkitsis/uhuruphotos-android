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
package com.savvasdalkitsis.uhuruphotos.feature.memories.domain.api.module

import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.module.FeedModule
import com.savvasdalkitsis.uhuruphotos.feature.memories.domain.api.usecase.MemoriesUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule

object MemoriesModule {

    val memoriesUseCase: MemoriesUseCase
        get() = com.savvasdalkitsis.uhuruphotos.feature.memories.domain.implementation.usecase.MemoriesUseCase(
            FeedModule.feedUseCase,
            DateModule.dateParser,
        )
}