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
package com.savvasdalkitsis.uhuruphotos.foundation.video.api.module.cache

import android.annotation.SuppressLint
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.module.SettingsModule
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.VideoCacheUseCase
import java.io.File

@SuppressLint("UnsafeOptInUsageError")
object VideoCacheModule {

    val videoCacheUseCase: VideoCacheUseCase
        get() = com.savvasdalkitsis.uhuruphotos.foundation.video.implementation.VideoCacheUseCase(
            videoCache
        )

    val videoCache: SimpleCache by singleInstance {
        SimpleCache(
            File(AndroidModule.applicationContext.cacheDir, "video_cache"),
            LeastRecentlyUsedCacheEvictor(
                SettingsModule.settingsUseCase.getVideoDiskCacheMaxLimit() * 1024L * 1024L
            ),
            StandaloneDatabaseProvider(AndroidModule.applicationContext)
        )
    }
}