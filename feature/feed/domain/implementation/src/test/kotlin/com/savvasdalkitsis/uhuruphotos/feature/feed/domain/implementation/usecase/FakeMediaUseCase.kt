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

import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSource
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHash
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstance
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import io.mockk.coEvery
import io.mockk.every
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf

fun MediaUseCase.defaults() = apply {
    mapsRemoteMediaCollections()
    hasNoLocalMedia()
}

fun MediaUseCase.hasNoLocalMedia() {
    every { observeLocalMedia() }.returns(flowOf(MediaItemsOnDevice.RequiresPermissions(emptyList())))
}

fun MediaUseCase.returnsLocalMedia(
    primaryFolder: Pair<LocalMediaFolder, List<MediaItem>>? = null,
    mediaFolders: List<Pair<LocalMediaFolder, List<MediaItem>>> = emptyList(),
) {
    every { observeLocalMedia() }.returns(flowOf(MediaItemsOnDevice.Found(primaryFolder, mediaFolders)))
}

private fun MediaUseCase.mapsRemoteMediaCollections() {
    val slot = slot<Group<String, MediaCollectionSource>>()
    coEvery { toMediaCollection(capture(slot)) }.answers {
        val groups = slot.captured
        groups.items.map { (id, source) ->
            MediaCollection(id, source.map {
                MediaItemInstance(
                    id = MediaId.Remote(it.id, it.isVideo, ""),
                    mediaHash = MediaItemHash(it.mediaItemId ?: "missing"),
                    displayDayDate = id,
                )
            }, id)
        }
    }
}
