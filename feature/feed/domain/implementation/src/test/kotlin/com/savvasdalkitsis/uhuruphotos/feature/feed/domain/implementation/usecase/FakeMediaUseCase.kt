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

import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSourceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstanceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDeviceModel
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
    every { observeLocalMedia() }.returns(flowOf(MediaItemsOnDeviceModel.RequiresPermissionsModel(emptyList())))
}

fun MediaUseCase.returnsLocalMedia(
    primaryFolder: Pair<LocalMediaFolder, List<MediaItemModel>>? = null,
    mediaFolders: List<Pair<LocalMediaFolder, List<MediaItemModel>>> = emptyList(),
) {
    every { observeLocalMedia() }.returns(flowOf(MediaItemsOnDeviceModel.FoundModel(primaryFolder, mediaFolders)))
}

private fun MediaUseCase.mapsRemoteMediaCollections() {
    val slot = slot<Group<String, MediaCollectionSourceModel>>()
    coEvery { toMediaCollection(capture(slot)) }.answers {
        val groups = slot.captured
        groups.items.map { (id, source) ->
            MediaCollectionModel(id, source.map {
                MediaItemInstanceModel(
                    id = MediaIdModel.RemoteIdModel(it.id, it.isVideo, MediaItemHashModel.fromRemoteMediaHash(it.mediaItemId ?: "missing", 0)),
                    mediaHash = MediaItemHashModel.fromRemoteMediaHash(it.mediaItemId ?: "missing", 0),
                    displayDayDate = id,
                )
            }, id)
        }
    }
}
