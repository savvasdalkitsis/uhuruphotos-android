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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.module.DbModule
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository.LocalMediaFolderRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository.LocalMediaRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository.MediaStoreVersionRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.LocalMediaService
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.usecase.BitmapUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule
import com.savvasdalkitsis.uhuruphotos.foundation.exif.api.module.ExifModule
import com.savvasdalkitsis.uhuruphotos.foundation.permissions.api.module.PermissionsModule
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.module.PreferencesModule
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.module.WorkerModule
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

object LocalMediaModule {

    val localMediaUseCase: LocalMediaUseCase
        get() = com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.usecase.LocalMediaUseCase(
            AndroidModule.applicationContext,
            localMediaDateTimeFormat,
            DateModule.parsingDateTimeFormat,
            DateModule.parsingDateFormat,
            DateModule.dateDisplayer,
            localMediaRepository,
            PermissionsModule.permissionFlow,
            localMediaFolderRepository,
            mediaStoreVersionRepository,
            WorkerModule.workerStatusUseCase,
            bitmapUseCase,
            localMediaWorkScheduler,
        )

    val localMediaRepository: LocalMediaRepository
        get() = LocalMediaRepository(
            DbModule.database,
            localMediaService,
            localMediaFolderRepository,
            AndroidModule.contentResolver,
            AndroidModule.applicationContext,
            ExifModule.exifUseCase,
            localMediaDateTimeFormat,
            bitmapUseCase,
            PreferencesModule.plainTextPreferences,
        )

    val localMediaWorkScheduler: LocalMediaWorkScheduler
        get() = com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.worker.LocalMediaWorkScheduler(
            WorkerModule.workScheduleUseCase,
        )

    val localMediaService: LocalMediaService
        get() = LocalMediaService(AndroidModule.applicationContext, AndroidModule.contentResolver)

    private val mediaStoreVersionRepository: MediaStoreVersionRepository
        get() = MediaStoreVersionRepository(
            localMediaService,
            PreferencesModule.plainTextPreferences,
        )

    private val bitmapUseCase: BitmapUseCase
        get() = BitmapUseCase(
            AndroidModule.applicationContext,
            ExifModule.exifUseCase,
        )

    private val localMediaFolderRepository: LocalMediaFolderRepository
        get() = LocalMediaFolderRepository(
            localMediaService,
            DbModule.database.localMediaItemDetailsQueries,
            PreferencesModule.plainTextPreferences,
        )

    private val localMediaDateTimeFormat: DateTimeFormatter
        get() = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss")
}