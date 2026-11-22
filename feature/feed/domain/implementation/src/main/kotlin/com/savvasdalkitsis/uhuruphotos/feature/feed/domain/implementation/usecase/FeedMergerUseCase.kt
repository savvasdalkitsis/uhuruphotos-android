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
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus.Processing
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.model.Uploads
import java.io.Serializable

internal class FeedMergerUseCase(
    private val allRemoteDays: Sequence<MediaCollectionModel>,
    private val allLocalMedia: List<MediaItemModel>,
    private val mediaBeingDownloaded: Set<String>,
    mediaBeingUploaded: Uploads,
) {

    private val processing = mediaBeingUploaded.filter { it == Processing }
    private val uploading = mediaBeingUploaded.filter { it != Processing }
    private val inFlight = processing + uploading
    private fun Uploads.filter(predicate: (UploadStatus) -> Boolean) =
        jobs.filter { predicate(it.status) }.map { it.localItemId }.toSet()

    fun mergeFeed(): List<MediaCollectionModel> {
        val remainingLocalMedia = RemainingLocalMedia(allLocalMedia)

        val merged = allRemoteDays
            .map { day ->
                day.mergeLocalMediaWithRemote(remainingLocalMedia)
            }
            .map { day ->
                day.addLocalMediaToDay(remainingLocalMedia)
            }
            .toList()

        val localOnlyDays = remainingLocalMedia.remaining()
            .groupBy { it.displayDayDate }
            .toMediaCollections()

        return (merged + localOnlyDays)
            .sortedByDescending { it.unformattedDate }
    }

    private fun MediaItemModel.orDownloading(inProgress: Set<String>): MediaItemModel =
        orIf<MediaIdModel.RemoteIdModel>(inProgress, MediaIdModel.RemoteIdModel::toDownloading)

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
            mediaItems = items.orUploadingOrProcessing(),
            displayTitle = day ?: "-",
            location = null,
            unformattedDate = items.firstOrNull()?.sortableDate
        )
    }

    private fun Collection<MediaItemModel>.orUploadingOrProcessing() = map {
        it.orIf<MediaIdModel.LocalIdModel>(inFlight) { model ->
            when (model.value) {
                in processing -> model.toProcessing()
                else -> model.toUploading()
            }
        }
    }

    private fun MediaCollectionModel.mergeLocalMediaWithRemote(
        remainingLocalMedia: RemainingLocalMedia
    ) = copy(mediaItems = mediaItems.map { item ->
        val local = remainingLocalMedia.takeSameContentAs(item)
        when {
            local.isEmpty() -> item.orDownloading(mediaBeingDownloaded)
            else -> {
                MediaItemGroupModel(
                    remoteInstance = item,
                    localInstances = local
                )
            }
        }
    })

    private fun MediaCollectionModel.addLocalMediaToDay(
        remainingLocalMedia: RemainingLocalMedia
    ): MediaCollectionModel {
        val local = remainingLocalMedia.takeFromDay(displayTitle)
        return when {
            local.isEmpty() -> this
            else -> {
                copy(mediaItems = (mediaItems + local.orUploadingOrProcessing())
                    .sortedByDescending { it.sortableDate }
                )
            }
        }
    }
}
