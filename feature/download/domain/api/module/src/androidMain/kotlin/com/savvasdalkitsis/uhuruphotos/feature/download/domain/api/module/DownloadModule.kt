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
package com.savvasdalkitsis.uhuruphotos.feature.download.domain.api.module

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.PlatformAuthModule
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.module.DbModule
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.module.CommonMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.module.LocalMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.module.RemoteMediaModule
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationWindowCallbacks
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCallbacks
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance
import com.savvasdalktsis.uhuruphotos.feature.download.domain.api.usecase.DownloadUseCase
import com.savvasdalktsis.uhuruphotos.feature.download.domain.implementation.initializer.DownloadActivityInitializer
import com.savvasdalktsis.uhuruphotos.feature.download.domain.implementation.initializer.DownloadInitializer
import com.savvasdalktsis.uhuruphotos.feature.download.domain.implementation.repository.DownloadingRepository

object DownloadModule {

    val downloadUseCase: DownloadUseCase
        get() = com.savvasdalktsis.uhuruphotos.feature.download.domain.implementation.usecase.DownloadUseCase(
            AndroidModule.downloadManager,
            downloadingRepository,
            CommonMediaModule.mediaUseCase,
            RemoteMediaModule.remoteMediaUseCase,
            PlatformAuthModule.authenticationHeadersUseCase,
            PlatformAuthModule.serverUseCase,
        )

    private val downloadingRepository: DownloadingRepository
        get() = DownloadingRepository(DbModule.database.downloadingMediaItemsQueries)

    val downloadInitializer: ApplicationCallbacks by singleInstance {
        DownloadInitializer(downloadUseCase)
    }

    val downloadActivityInitializer: ApplicationWindowCallbacks by singleInstance {
        DownloadActivityInitializer(LocalMediaModule.localMediaWorkScheduler)
    }
}