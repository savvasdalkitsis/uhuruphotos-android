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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.GetRemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedImmediateWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSource
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemGroup
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstance
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice.Error
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice.Found
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice.RequiresPermissions
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.mapValues
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.observe
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import com.savvasdalktsis.uhuruphotos.foundation.download.api.usecase.DownloadUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class FeedUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
    private val mediaUseCase: MediaUseCase,
    private val feedImmediateWorkScheduler: FeedImmediateWorkScheduler,
    private val downloadUseCase: DownloadUseCase,
    private val preferences: Preferences,
) : FeedUseCase {

    private val key = "feedDisplay"

    override fun observeFeed(): Flow<List<MediaCollection>> =
        combine(observeRemoteMediaFeed(), observeLocalMediaFeed(), observeDownloading(), ::mergeRemoteWithLocalMedia)

    override suspend fun getFeed(): List<MediaCollection> = mergeRemoteWithLocalMedia(
        getRemoteMediaFeed(), getLocalMediaFeed(), getDownloading(),
    )

    private fun mergeRemoteWithLocalMedia(
        allRemoteDays: List<MediaCollection>,
        allLocalMedia: List<MediaItem>,
        mediaBeingDownloaded: Set<String>,
    ): List<MediaCollection> {
        val allLocalDays = allLocalMedia.groupBy { it.displayDayDate }
        val combined = mergeLocalMediaIntoExistingRemoteDays(allRemoteDays, allLocalDays) +
                remainingLocalDays(allRemoteDays, allLocalDays)
        return combined.sortedByDescending { it.unformattedDate }.markDownloading(mediaBeingDownloaded)
    }

    private fun List<MediaCollection>.markDownloading(
        inProgress: Set<String>,
    ): List<MediaCollection> = map { collection ->
        collection.copy(mediaItems = collection.mediaItems.map { mediaItem ->
            val id = mediaItem.id
            if (mediaItem is MediaItemInstance
                && id is MediaId.Remote
                && id.value in inProgress
            ) {
                mediaItem.copy(id = MediaId.Downloading(id.value, id.isVideo, id.serverUrl))
            } else {
                mediaItem
            }
        })
    }

    override suspend fun refreshCluster(clusterId: String) {
        feedRepository.refreshRemoteMediaCollection(clusterId)
    }

    private fun Flow<List<MediaCollection>>.initialize() = distinctUntilChanged()
        .safelyOnStartIgnoring {
            if (!feedRepository.hasRemoteMediaCollections()) {
                refreshFeed(shallow = false)
            }
        }

    override suspend fun hasFeed(): Boolean =
        feedRepository.hasRemoteMediaCollections()

    override fun getFeedDisplay(): Flow<PredefinedCollageDisplay> =
        preferences.observe(key, PredefinedCollageDisplay.default)

    override fun setFeedDisplay(feedDisplay: PredefinedCollageDisplay) {
        preferences.set(key, feedDisplay)
    }

    override fun refreshFeed(shallow: Boolean) {
        feedImmediateWorkScheduler.scheduleFeedRefreshNow(shallow)
    }

    private fun GetRemoteMediaCollections.toMediaCollectionSource() = MediaCollectionSource(
        id = id,
        date = albumDate,
        location = albumLocation,
        mediaItemId = photoId,
        dominantColor = dominantColor,
        rating = rating,
        aspectRatio = aspectRatio,
        isVideo = isVideo,
    )

    private suspend fun Group<String, MediaCollectionSource>.toCollection() =
        with(mediaUseCase) {
            toMediaCollection()
        }

    private fun mergeLocalMediaIntoExistingRemoteDays(
        remoteDays: List<MediaCollection>,
        localDays: Map<String?, List<MediaItem>>
    ) = remoteDays.map { remoteDay ->
        val localMediaOnDay = localDays[remoteDay.displayTitle] ?: emptyList()
        val existingRemoteMediaHashes = remoteDay.mediaItems.map { it.mediaHash }
        val localMediaNotPresentRemotely = localMediaOnDay.filter {
            it.mediaHash !in existingRemoteMediaHashes
        }
        val combinedMediaItems = remoteDay.mediaItems.map { remoteMediaItem ->
            mergeLocalDuplicates(localDays.flatMap { it.value }, remoteMediaItem)
        } + localMediaNotPresentRemotely
        remoteDay.copy(
            mediaItems = combinedMediaItems.sortedByDescending { it.sortableDate }
        )
    }

    private fun mergeLocalDuplicates(
        allLocalMedia: List<MediaItem>,
        remoteMediaItem: MediaItem
    ): MediaItem {
        val localCopies =
            allLocalMedia.filter { it.mediaHash == remoteMediaItem.mediaHash }.toSet()
        return when {
            localCopies.isNotEmpty() ->
                MediaItemGroup(
                    remoteInstance = remoteMediaItem,
                    localInstances = localCopies,
                )
            else -> remoteMediaItem
        }
    }

    private fun remainingLocalDays(
        allRemoteDays: List<MediaCollection>,
        allLocalDays: Map<String?, List<MediaItem>>
    ) = allLocalDays
        .filter { (day, _) -> day !in allRemoteDays.map { it.displayTitle } }
        .map { (day, items) ->
            MediaCollection(
                id = "local_media_collection_$day",
                mediaItems = items,
                displayTitle = day ?: "-",
                location = null,
                unformattedDate = items.firstOrNull()?.sortableDate
            )
        }

    private fun observeLocalMediaFeed() = mediaUseCase.observeLocalMedia()
        .distinctUntilChanged()
        .map {
            when (it) {
                is Found -> it.primaryFolder?.second ?: emptyList()
                else -> emptyList()
            }
        }

    private fun observeDownloading() = downloadUseCase.observeDownloading().map {
        it
    }

    private fun observeRemoteMediaFeed() = feedRepository.observeRemoteMediaCollectionsByDate()
        .distinctUntilChanged()
        .map {
            it.mapValues { albums ->
                albums.toMediaCollectionSource()
            }
        }.map {
            it.toCollection()
        }.initialize()


    private suspend fun getRemoteMediaFeed() = feedRepository.getRemoteMediaCollectionsByDate()
        .mapValues { it.toMediaCollectionSource() }
        .toCollection()

    private suspend fun getLocalMediaFeed(): List<MediaItem> = when (val mediaOnDevice = mediaUseCase.getLocalMedia()) {
        Error -> emptyList()
        is Found -> mediaOnDevice.primaryFolder?.second ?: emptyList()
        is RequiresPermissions -> emptyList()
    }

    private suspend fun getDownloading(): Set<String> = downloadUseCase.getDownloading()

}
