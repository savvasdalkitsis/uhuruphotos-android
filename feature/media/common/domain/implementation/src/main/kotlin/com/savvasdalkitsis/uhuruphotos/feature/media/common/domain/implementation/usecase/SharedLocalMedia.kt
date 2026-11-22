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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDeviceModel
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.reportingErrors
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.shareIn
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedLocalMedia internal constructor(private val scope: CoroutineScope) {

    @Inject
    constructor() : this(CoroutineScope(SupervisorJob() + Dispatchers.Default + reportingErrors))

    private val shared = AtomicReference<Flow<MediaItemsOnDeviceModel>?>()

    fun share(media: () -> Flow<MediaItemsOnDeviceModel>): Flow<MediaItemsOnDeviceModel> =
        shared.get() ?: start(media)

    private fun start(media: () -> Flow<MediaItemsOnDeviceModel>): Flow<MediaItemsOnDeviceModel> {
        lateinit var started: Flow<MediaItemsOnDeviceModel>
        started = media()
            .catch { log(it) }
            .onCompletion { shared.compareAndSet(started, null) }
            .shareIn(scope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS, replayExpirationMillis = 0), replay = 1)
        return if (shared.compareAndSet(null, started)) started else shared.get() ?: started
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
