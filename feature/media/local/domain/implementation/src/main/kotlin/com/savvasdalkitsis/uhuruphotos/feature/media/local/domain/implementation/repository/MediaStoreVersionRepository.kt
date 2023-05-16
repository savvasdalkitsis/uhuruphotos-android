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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository

import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.LocalMediaService
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import javax.inject.Inject

class MediaStoreVersionRepository @Inject constructor(
    private val localMediaService: LocalMediaService,
    private val preferences: Preferences,
) {
    private val key = "media_store_version"

    var currentMediaStoreVersion
        get() = preferences.get(key, defaultValue = "")
        set(value) { preferences.set(key, value) }

    val latestMediaStoreVersion get() = localMediaService.mediaStoreCurrentVersion
}