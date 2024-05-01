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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.module

import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MetadataUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.module.LocalMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.module.RemoteMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.module.UserModule
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule
import com.savvasdalkitsis.uhuruphotos.foundation.exif.api.module.ExifModule
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.module.cache.ImageCacheModule

object CommonMediaModule {

    val mediaUseCase: MediaUseCase
        get() = com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.implementation.usecase.MediaUseCase(
            LocalMediaModule.localMediaUseCase,
            RemoteMediaModule.remoteMediaUseCase,
            UserModule.userUseCase,
            DateModule.dateDisplayer,
            DateModule.dateParser,
            AndroidModule.applicationContext,
        )

    val metadataUseCase: MetadataUseCase
        get() = com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.implementation.usecase.MetadataUseCase(
            ImageCacheModule.fullImageDiskCache,
            ExifModule.exifUseCase,
            AndroidModule.contentResolver,
        )
}