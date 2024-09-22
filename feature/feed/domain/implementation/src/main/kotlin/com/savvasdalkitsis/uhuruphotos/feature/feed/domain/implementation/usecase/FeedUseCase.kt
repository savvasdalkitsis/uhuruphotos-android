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

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.GetRemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.upload.ProcessingMediaItems
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.portfolio.PortfolioItems
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType.ALL
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType.ONLY_WITHOUT_DATES
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType.ONLY_WITH_DATES
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType.VIDEOS
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedCache
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSource
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemGroup
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstance
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice.Found
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.api.usecase.PortfolioUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.WelcomeUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.flow
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.andThenSwitchTo
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.mapValues
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.observe
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import com.savvasdalktsis.uhuruphotos.feature.download.domain.api.usecase.DownloadUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import se.ansman.dagger.auto.AutoBind
import java.io.Serializable
import javax.inject.Inject

@AutoBind
internal class FeedUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
    private val feedCache: FeedCache,
    private val mediaUseCase: MediaUseCase,
    private val feedWorkScheduler: FeedWorkScheduler,
    private val downloadUseCase: DownloadUseCase,
    private val uploadUseCase: UploadUseCase,
    @PlainTextPreferences
    private val preferences: Preferences,
    private val welcomeUseCase: WelcomeUseCase,
    private val portfolioUseCase: PortfolioUseCase,
) : FeedUseCase {

    private val key = "feedDisplay"

    override fun observeFeed(
        feedFetchType: FeedFetchType,
        loadSmallInitialChunk: Boolean,
    ): Flow<List<MediaCollection>> = when {
        loadSmallInitialChunk -> combinedFeed(feedFetchType, true)
            .andThenSwitchTo(
                combinedFeed(feedFetchType, false),
            )
        else -> combinedFeed(feedFetchType, false)
    }.distinctUntilChanged()

    private fun combinedFeed(
        feedFetchType: FeedFetchType,
        loadSmallInitialChunk: Boolean
    ) = merge(
            feedCache.observeFeed(feedFetchType, loadSmallInitialChunk),
            liveFeed(feedFetchType, loadSmallInitialChunk).onEach {
                feedCache.cacheFeed(it, feedFetchType, loadSmallInitialChunk)
            }
        )

    private fun liveFeed(
        feedFetchType: FeedFetchType,
        loadSmallInitialChunk: Boolean
    ) = combine(
        observeRemoteMediaFeed(feedFetchType, loadSmallInitialChunk),
        observeLocalMediaFeed(feedFetchType),
        observeDownloading(),
        observeUploading(),
        observeProcessing().map { it.map(ProcessingMediaItems::id).toSet() },
        ::mergeRemoteWithLocalMedia
    ).debounce(500)

    private fun mergeRemoteWithLocalMedia(
        allRemoteDays: Sequence<MediaCollection>,
        allLocalMedia: List<MediaItem>,
        mediaBeingDownloaded: Set<String>,
        mediaBeingUploaded: Set<Long>,
        mediaBeingProcessed: Set<Long>,
    ): List<MediaCollection> {
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

    private fun MediaItem.orDownloading(inProgress: Set<String>): MediaItem =
        orIf<MediaId.Remote>(inProgress, MediaId.Remote::toDownloading)

    private fun MediaItem.orUploading(inProgress: Set<Long>): MediaItem =
        orIf<MediaId.Local>(inProgress, MediaId.Local::toUploading)

    private fun MediaItem.orProcessing(inProgress: Set<Long>): MediaItem =
        orIf<MediaId.Local>(inProgress, MediaId.Local::toProcessing)

    private inline fun <reified M> MediaItem.orIf(set: Set<Serializable>, map: (M) -> MediaId<*>): MediaItem {
        val id = id
        return if (this is MediaItemInstance && id is M && id.value in set)
            copy(id = map(id))
        else
            this
    }

    override suspend fun refreshCluster(clusterId: String) =
        feedRepository.refreshRemoteMediaCollection(clusterId)

    private fun Flow<Sequence<MediaCollection>>.initialize() = distinctUntilChanged()
        .safelyOnStartIgnoring {
            scheduleFeedRefreshNow()
        }

    override suspend fun hasFeed(): Boolean =
        feedRepository.hasRemoteMediaCollections()

    override fun observeFeedDisplay(): Flow<PredefinedCollageDisplayState> =
        preferences.observe(key, PredefinedCollageDisplayState.default)

    override fun setFeedDisplay(feedDisplay: PredefinedCollageDisplayState) {
        preferences.set(key, feedDisplay)
    }

    override fun scheduleFeedRefreshNow() {
        feedWorkScheduler.scheduleFeedRefreshNow()
    }

    override fun invalidateRemoteFeed() {
        feedRepository.invalidateRemoteFeed()
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
            mediaUseCase.toMediaCollection(this@toCollection)

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
        combine(
            mediaUseCase.observeLocalMedia()
                .distinctUntilChanged(),
            portfolioUseCase.observePublishedFolderIds(),
            portfolioUseCase.observeIndividualPortfolioItems(),
        ) { itemsOnDevice, publishedFolderIds, publishedItems ->
            val publishedItemsNotInPublishedFolders = publishedItems
                .filter { it.folderId !in publishedFolderIds }
                .map(PortfolioItems::id)
            when (itemsOnDevice) {
                is Found -> itemsOnDevice.filteredWith(
                    publishedFolderIds,
                    publishedItemsNotInPublishedFolders,
                    feedFetchType,
                )
                else -> emptyList()
            }
        }

    private fun observeDownloading() = downloadUseCase.observeDownloading()

    private fun observeUploading() = uploadUseCase.observeUploading()

    private fun observeProcessing() = uploadUseCase.observeProcessing()

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

    private fun Found.filteredWith(
        publishedFolderIds: Set<Int>,
        publishedItemsIds: List<Long>,
        feedFetchType: FeedFetchType
    ): List<MediaItem> {
        val primaryFolderItems = primaryFolder?.second ?: emptyList()
        val publishedFoldersItems = mediaFolders.filter { (folder, _) ->
            folder.id in publishedFolderIds
        }.flatMap { (_, mediaItems) -> mediaItems }
        val publishedItems = mediaFolders.flatMap { (_, items) -> items }
            .filter { it.id.value in publishedItemsIds }
        return (primaryFolderItems + publishedFoldersItems + publishedItems).filter(feedFetchType)
    }

    private fun List<MediaItem>.filter(
        feedFetchType: FeedFetchType
    ) = filter { item ->
        when (feedFetchType) {
            ALL -> true
            ONLY_WITH_DATES -> !item.displayDayDate.isNullOrEmpty()
            ONLY_WITHOUT_DATES -> item.displayDayDate.isNullOrEmpty()
            VIDEOS -> item.id.isVideo
        }
    }
}
