/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.worker

import kotlinx.coroutines.CoroutineDispatcher
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

object RemoteMediaDownloadDispatcher : CoroutineDispatcher() {
    private val executor = Executors.newSingleThreadExecutor {
        Thread(it, RemoteMediaItemOriginalFileRetrieveWorker::class.java.name).apply {
            priority = Thread.MIN_PRIORITY
        }
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) =
        executor.execute(block)
}