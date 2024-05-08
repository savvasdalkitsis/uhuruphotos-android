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
package com.savvasdalkitsis.uhuruphotos.feature.upload.domain.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.PlatformAuthModule
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.module.DbModule
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.module.LocalMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.module.RemoteMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.module.SettingsModule
import com.savvasdalkitsis.uhuruphotos.feature.site.domain.api.module.SiteModule
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.work.UploadWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.implementation.repository.UploadRepository
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.implementation.service.UploadService
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.implementation.usecase.ChunkedUploader
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.module.UserModule
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.module.WelcomeModule
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.module.WorkerModule

object UploadModule {

    val uploadUseCase: UploadUseCase
        get() = com.savvasdalkitsis.uhuruphotos.feature.upload.domain.implementation.usecase.UploadUseCase(
            SiteModule.siteUseCase,
            UserModule.userUseCase,
            uploadRepository,
            uploadService,
            LocalMediaModule.localMediaUseCase,
            RemoteMediaModule.remoteMediaUseCase,
            uploadWorkScheduler,
            chunkedUploader,
            SettingsModule.settingsUseCase,
            WelcomeModule.welcomeUseCase,
        )

    val uploadRepository: UploadRepository
        get() = UploadRepository(
            DbModule.database.uploadingMediaItemsQueries,
            DbModule.database.processingMediaItemsQueries,
            DbModule.database,
        )

    val uploadWorkScheduler: UploadWorkScheduler
        get() = com.savvasdalkitsis.uhuruphotos.feature.upload.domain.implementation.work.UploadWorkScheduler(
            WorkerModule.workScheduleUseCase,
            WorkerModule.workerStatusUseCase,
        )

    private val chunkedUploader: ChunkedUploader
        get() = ChunkedUploader(
            uploadRepository,
            AndroidModule.contentResolver,
            uploadService,
        )

    private val uploadService: UploadService by singleInstance {
        PlatformAuthModule.ktorfit.create()
    }
}