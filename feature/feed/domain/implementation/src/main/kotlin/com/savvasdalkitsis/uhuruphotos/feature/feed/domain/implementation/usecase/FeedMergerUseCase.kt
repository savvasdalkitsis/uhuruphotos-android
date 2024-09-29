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
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemGroupModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstanceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemModel
import java.io.Serializable

internal class FeedMergerUseCase(
    private val allRemoteDays: Sequence<MediaCollectionModel>,
    private val allLocalMedia: List<MediaItemModel>,
    private val mediaBeingDownloaded: Set<String>,
    private val mediaBeingUploaded: Set<Long>,
    private val mediaBeingProcessed: Set<Long>,
) {

    fun mergeFeed(): List<MediaCollectionModel> {
        val remainingLocalMedia = allLocalMedia.toMutableList()

        val merged = allRemoteDays
            .map { day ->
                day.copy(mediaItems = day.mediaItems.map { item ->
                    val local = remainingLocalMedia
                        .filter { it.mediaHash == item.mediaHash }
                        .toSet()
                    remainingLocalMedia.removeAll(local)
                    when {
                        local.isEmpty() -> item.orDownloading(mediaBeingDownloaded)
                        else -> MediaItemGroupModel(
                            remoteInstance = item,
                            localInstances = local.toSet()
                        )
                    }
                })
            }
            .map { collection ->
                val local = remainingLocalMedia
                    .filter { it.displayDayDate == collection.displayTitle }
                    .toSet()
                remainingLocalMedia.removeAll(local)
                when {
                    local.isEmpty() -> collection
                    else -> collection.copy(mediaItems = (collection.mediaItems + local)
                        .sortedByDescending { it.sortableDate }
                    )
                }
            }
            .toList()

        val localOnlyDays = remainingLocalMedia
            .map { it.orUploading(mediaBeingUploaded).orProcessing(mediaBeingProcessed) }
            .groupBy { it.displayDayDate }
            .toMediaCollections()

        return (merged + localOnlyDays)
            .sortedByDescending { it.unformattedDate }
            .toList()
    }

    private fun MediaItemModel.orDownloading(inProgress: Set<String>): MediaItemModel =
        orIf<MediaIdModel.RemoteIdModel>(inProgress, MediaIdModel.RemoteIdModel::toDownloading)

    private fun MediaItemModel.orUploading(inProgress: Set<Long>): MediaItemModel =
        orIf<MediaIdModel.LocalIdModel>(inProgress, MediaIdModel.LocalIdModel::toUploading)

    private fun MediaItemModel.orProcessing(inProgress: Set<Long>): MediaItemModel =
        orIf<MediaIdModel.LocalIdModel>(inProgress, MediaIdModel.LocalIdModel::toProcessing)

    private inline fun <reified M> MediaItemModel.orIf(set: Set<Serializable>, map: (M) -> MediaIdModel<*>): MediaItemModel {
        val id = id
        return if (this is MediaItemInstanceModel && id is M && id.value in set)
            copy(id = map(id))
        else
            this
    }

    private fun Map<String?, List<MediaItemModel>>.toMediaCollections() = map { (day, items) ->
        MediaCollectionModel(
            id = "local_media_collection_$day",
            mediaItems = items,
            displayTitle = day ?: "-",
            location = null,
            unformattedDate = items.firstOrNull()?.sortableDate
        )
    }

}