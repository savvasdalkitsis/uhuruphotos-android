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
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType.ALL
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType.ONLY_WITHOUT_DATES
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType.ONLY_WITH_DATES
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType.VIDEOS
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
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
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.WelcomeUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.mapValues
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.observe
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import com.savvasdalktsis.uhuruphotos.feature.download.domain.api.usecase.DownloadUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
internal class FeedUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
    private val mediaUseCase: MediaUseCase,
    private val feedWorkScheduler: FeedWorkScheduler,
    private val downloadUseCase: DownloadUseCase,
    private val uploadUseCase: UploadUseCase,
    private val preferences: Preferences,
    private val welcomeUseCase: WelcomeUseCase,
) : FeedUseCase {

    private val key = "feedDisplay"

    override fun observeFeed(
        feedFetchType: FeedFetchType,
        loadSmallInitialChunk: Boolean,
    ): Flow<List<MediaCollection>> =
        combine(
            observeRemoteMediaFeed(feedFetchType, loadSmallInitialChunk),
            observeLocalMediaFeed(feedFetchType),
            observeDownloading(),
            observeUploading(),
            ::mergeRemoteWithLocalMedia
        ).debounce(500)

    override suspend fun getFeed(
        feedFetchType: FeedFetchType,
    ): List<MediaCollection> = mergeRemoteWithLocalMedia(
        getRemoteMediaFeed(feedFetchType),
        getLocalMediaFeed(feedFetchType),
        getDownloading(),
        getUploading(),
    )

    private fun mergeRemoteWithLocalMedia(
        allRemoteDays: Sequence<MediaCollection>,
        allLocalMedia: List<MediaItem>,
        mediaBeingDownloaded: Set<String>,
        mediaBeingUploaded: Set<Long>,
    ): List<MediaCollection> {
        val remainingLocalMedia = allLocalMedia.toMutableList()

        val merged = allRemoteDays
            .map { day ->
                day.copy(mediaItems = day.mediaItems.map { item ->
                    val local = remainingLocalMedia
                        .filter { it.mediaHash == item.mediaHash }
                    remainingLocalMedia.removeAll(local)
                    when {
                        local.isEmpty() -> item.orDownloading(mediaBeingDownloaded)
                        else -> MediaItemGroup(
                            remoteInstance = item,
                            localInstances = local.toSet()
                        )
                    }
                })
            }
            .map { collection ->
                val local = remainingLocalMedia
                    .filter { it.displayDayDate == collection.displayTitle }
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
            .map { it.orUploading(mediaBeingUploaded) }
            .groupBy { it.displayDayDate }
            .toMediaCollections()

        return (merged + localOnlyDays)
            .sortedByDescending { it.unformattedDate }
            .toList()
    }

    private fun MediaItem.orDownloading(inProgress: Set<String>): MediaItem {
        val id = id
        return if (this is MediaItemInstance && id is MediaId.Remote && id.value in inProgress)
            copy(id = id.toDownloading())
        else
            this
    }

    private fun MediaItem.orUploading(inProgress: Set<Long>): MediaItem {
        val id = id
        return if (this is MediaItemInstance && id is MediaId.Local && id.value in inProgress)
            copy(id = id.toUploading())
        else
            this
    }

    override suspend fun refreshCluster(clusterId: String) =
        feedRepository.refreshRemoteMediaCollection(clusterId)

    private fun Flow<Sequence<MediaCollection>>.initialize() = distinctUntilChanged()
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
        feedWorkScheduler.scheduleFeedRefreshNow(shallow)
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
        lat = gpsLat,
        lon = gpsLon,
    )

    private suspend fun Group<String, MediaCollectionSource>.toCollection() =
        with(mediaUseCase) {
            toMediaCollection()
        }

    private fun Map<String?, List<MediaItem>>.toMediaCollections() = map { (day, items) ->
        MediaCollection(
            id = "local_media_collection_$day",
            mediaItems = items,
            displayTitle = day ?: "-",
            location = null,
            unformattedDate = items.firstOrNull()?.sortableDate
        )
    }

    private fun observeLocalMediaFeed(feedFetchType: FeedFetchType) =
        mediaUseCase.observeLocalMedia()
            .distinctUntilChanged()
            .map {
                when (it) {
                    is Found -> it.filteredWith(feedFetchType)
                    else -> emptyList()
                }
            }

    private fun observeDownloading() = downloadUseCase.observeDownloading()

    private fun observeUploading() = uploadUseCase.observeUploading()

    private fun observeRemoteMediaFeed(
        feedFetchType: FeedFetchType,
        loadSmallInitialChunk: Boolean,
    ) = welcomeUseCase.flow(
        withoutRemoteAccess = flowOf(emptySequence()),
        withRemoteAccess = feedRepository.observeRemoteMediaCollectionsByDate(
            feedFetchType,
            loadSmallInitialChunk
        ).distinctUntilChanged()
            .map {
                it.mapValues { albums ->
                    albums.toMediaCollectionSource()
                }
            }.map {
                it.toCollection().asSequence()
            }.initialize()
        )

    private suspend fun getRemoteMediaFeed(
        feedFetchType: FeedFetchType
    ) = feedRepository.getRemoteMediaCollectionsByDate(feedFetchType)
        .mapValues { it.toMediaCollectionSource() }
        .toCollection()
        .asSequence()

    private suspend fun getLocalMediaFeed(feedFetchType: FeedFetchType) =
        when (val mediaOnDevice = mediaUseCase.getLocalMedia()) {
            Error -> emptyList()
            is Found -> mediaOnDevice.filteredWith(feedFetchType)
            is RequiresPermissions -> emptyList()
        }

    private suspend fun getDownloading(): Set<String> = downloadUseCase.getDownloading()

    private fun Found.filteredWith(feedFetchType: FeedFetchType) =
        (primaryFolder?.second ?: emptyList()).filter { item ->
            when (feedFetchType) {
                ALL -> true
                ONLY_WITH_DATES -> !item.displayDayDate.isNullOrEmpty()
                ONLY_WITHOUT_DATES -> item.displayDayDate.isNullOrEmpty()
                VIDEOS -> item.id.isVideo
            }
        }

    private suspend fun getUploading(): Set<Long> = uploadUseCase.observeUploading().first()

}
