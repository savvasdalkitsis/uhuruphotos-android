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
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchTypeModel
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchTypeModel.ALL
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchTypeModel.ONLY_WITHOUT_DATES
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchTypeModel.ONLY_WITH_DATES
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchTypeModel.VIDEOS
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedCache
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSourceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDeviceModel.FoundModel
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
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
        feedFetchTypeModel: FeedFetchTypeModel,
        loadSmallInitialChunk: Boolean,
    ): Flow<List<MediaCollectionModel>> = when {
        loadSmallInitialChunk -> combinedFeed(feedFetchTypeModel, true)
            .andThenSwitchTo(
                combinedFeed(feedFetchTypeModel, false),
            )
        else -> combinedFeed(feedFetchTypeModel, false)
    }.distinctUntilChanged()

    private fun combinedFeed(
        feedFetchTypeModel: FeedFetchTypeModel,
        loadSmallInitialChunk: Boolean
    ) = merge(
            feedCache.observeFeed(feedFetchTypeModel, loadSmallInitialChunk),
            liveFeed(feedFetchTypeModel, loadSmallInitialChunk).onEach {
                feedCache.cacheFeed(it, feedFetchTypeModel, loadSmallInitialChunk)
            }
        )

    private fun liveFeed(
        feedFetchTypeModel: FeedFetchTypeModel,
        loadSmallInitialChunk: Boolean
    ) = combine(
        observeRemoteMediaFeed(feedFetchTypeModel, loadSmallInitialChunk),
        observeLocalMediaFeed(feedFetchTypeModel),
        observeDownloading(),
        observeUploading(),
        observeProcessing().map { it.map(ProcessingMediaItems::id).toSet() },
        ::FeedMergerUseCase
    ).mapLatest {
        it.mergeFeed()
    }

    override suspend fun refreshCluster(clusterId: String) =
        feedRepository.refreshRemoteMediaCollection(clusterId)

    private fun Flow<Sequence<MediaCollectionModel>>.initialize() = distinctUntilChanged()
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

    private fun GetRemoteMediaCollections.toMediaCollectionSource() = MediaCollectionSourceModel(
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

    private suspend fun Group<String, MediaCollectionSourceModel>.toCollection() =
            mediaUseCase.toMediaCollection(this@toCollection)

    private fun observeLocalMediaFeed(feedFetchTypeModel: FeedFetchTypeModel) =
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
                is FoundModel -> itemsOnDevice.filteredWith(
                    publishedFolderIds,
                    publishedItemsNotInPublishedFolders,
                    feedFetchTypeModel,
                )
                else -> emptyList()
            }
        }

    private fun observeDownloading() = downloadUseCase.observeDownloading()

    private fun observeUploading() = uploadUseCase.observeUploading()

    private fun observeProcessing() = uploadUseCase.observeProcessing()

    private fun observeRemoteMediaFeed(
        feedFetchTypeModel: FeedFetchTypeModel,
        loadSmallInitialChunk: Boolean,
    ) = welcomeUseCase.flow(
        withoutRemoteAccess = flowOf(emptySequence()),
        withRemoteAccess = feedRepository.observeRemoteMediaCollectionsByDate(
            feedFetchTypeModel,
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

    private fun FoundModel.filteredWith(
        publishedFolderIds: Set<Int>,
        publishedItemsIds: List<Long>,
        feedFetchTypeModel: FeedFetchTypeModel
    ): List<MediaItemModel> {
        val primaryFolderItems = primaryFolder?.second ?: emptyList()
        val publishedFoldersItems = mediaFolders.filter { (folder, _) ->
            folder.id in publishedFolderIds
        }.flatMap { (_, mediaItems) -> mediaItems }
        val publishedItems = mediaFolders.flatMap { (_, items) -> items }
            .filter { it.id.value in publishedItemsIds }
        return (primaryFolderItems + publishedFoldersItems + publishedItems).filter(feedFetchTypeModel)
    }

    private fun List<MediaItemModel>.filter(
        feedFetchTypeModel: FeedFetchTypeModel
    ) = filter { item ->
        when (feedFetchTypeModel) {
            ALL -> true
            ONLY_WITH_DATES -> !item.displayDayDate.isNullOrEmpty()
            ONLY_WITHOUT_DATES -> item.displayDayDate.isNullOrEmpty()
            VIDEOS -> item.id.isVideo
        }
    }
}
