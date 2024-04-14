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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.usecase

import com.savvasdalktsis.uhuruphotos.feature.download.domain.api.usecase.DownloadUseCase
import io.mockk.every
import kotlinx.coroutines.flow.flowOf

fun DownloadUseCase.defaults() = apply {
    hasNoDownloadsInProgress()
}

fun DownloadUseCase.hasNoDownloadsInProgress() {
    every { observeDownloading() }.returns(flowOf(emptySet()))
}

fun DownloadUseCase.isDownloading(vararg ids: String) {
    every { observeDownloading() }.returns(flowOf(ids.toSet()))
}