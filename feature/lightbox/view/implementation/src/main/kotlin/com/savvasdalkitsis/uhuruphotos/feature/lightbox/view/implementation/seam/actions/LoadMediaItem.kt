/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions

import com.github.michaelbull.result.getOr
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.portfolio.PortfolioItems
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.FavouriteMedia
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.Feed
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.HiddenMedia
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.LocalAlbum
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.Memory
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.PersonResults
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.SearchResults
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.Single
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.Trash
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.Undated
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.UserAlbum
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.Videos
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.FinishedLoading
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.FinishedLoadingDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.LoadingDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ReceivedDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowMedia
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowRestoreButton
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.MediaItemType
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach

data class LoadMediaItem(
    val actionMediaId: MediaId<*>,
    val sequenceDataSource: LightboxSequenceDataSource,
) : LightboxAction() {

    context(LightboxActionsContext) override fun handle(
        state: LightboxState,
    ) = merge(
        flow {
            currentMediaId.emit(actionMediaId)
            emit(ShowMedia(listOf(actionMediaId.toSingleMediaItemState()), 0))

            if (sequenceDataSource == Trash) {
                mediaItemType = MediaItemType.TRASHED
                emit(ShowRestoreButton)
            }
        },
        combine(
            observeMediaSequence()
                .filter { it.isNotEmpty() },
            serverUseCase.observeMaybeServerUrl(),
            currentMediaId,
        ) { media, serverUrl, id ->
            Triple(media, serverUrl.orEmpty(), id)
        }.flatMapLatest { (media, serverUrl, id) ->
            channelFlow {
                ShowMedia(media, id)?.let {
                    send(it)
                    send(Loading)
                    send(LoadingDetails(id))
                    lightboxUseCase.observeLightboxItemDetails(id).map { details ->
                        ReceivedDetails(id, details, serverUrl)
                    }.onEach {
                        send(FinishedLoading)
                        send(FinishedLoadingDetails(id))
                    }.collect(this::send)
                }
            }
        }
    )

    context(LightboxActionsContext)
    private fun observeMediaSequence(): Flow<List<SingleMediaItemState>> =
        combine(
            observeMediaItemsSequence(),
            isInPortfolio(),
        ) { mediaItems, isInPortfolio ->
            val addToPortfolioEnabled = addToPortfolioIconEnabled()
            val showAddToPortfolioIcon = sequenceDataSource is LocalAlbum
                    && sequenceDataSource.albumId != localMediaUseCase.getDefaultFolderId()
            mediaItems.map {
                it.toSingleMediaItemState(isInPortfolio, showAddToPortfolioIcon, addToPortfolioEnabled)
            }
        }

    context(LightboxActionsContext)
    private fun observeMediaItemsSequence(): Flow<List<MediaItem>> = when (sequenceDataSource) {
        Single -> emptyFlow()
        Feed -> feedUseCase.observeFeed(FeedFetchType.ONLY_WITH_DATES, loadSmallInitialChunk = false).toMediaItems
        is Memory -> memoriesUseCase.observeMemories(loadSmallInitialChunk = false).map { collections ->
            collections.find { it.yearsAgo == sequenceDataSource.yearsAgo }?.mediaCollection?.mediaItems
            ?: emptyList()
        }
        is SearchResults -> searchUseCase.searchFor(sequenceDataSource.query)
            .mapNotNull { it.getOr(null) }.toMediaItems
        is PersonResults -> personUseCase.observePersonMedia(sequenceDataSource.personId).toMediaItems
        is AutoAlbum -> autoAlbumUseCase.observeAutoAlbum(sequenceDataSource.albumId).toMediaItems
        is UserAlbum -> userAlbumUseCase.observeUserAlbum(sequenceDataSource.albumId)
            .map { it.mediaCollections }.toMediaItems
        is LocalAlbum -> localAlbumUseCase.observeLocalAlbum(sequenceDataSource.albumId)
                .map { it.second }.toMediaItems
        FavouriteMedia -> mediaUseCase.observeFavouriteMedia().map { it.getOr(emptyList()) }
        HiddenMedia -> mediaUseCase.observeHiddenMedia().map { it.getOr(emptyList()) }
        Trash -> trashUseCase.observeTrashAlbums().toMediaItems
        Videos -> feedUseCase.observeFeed(FeedFetchType.VIDEOS).toMediaItems
        Undated -> feedUseCase.observeFeed(FeedFetchType.ONLY_WITHOUT_DATES).toMediaItems
    }

    context(LightboxActionsContext)
    private fun isInPortfolio(): Flow<(Long) -> Boolean> = when {
        isViewingLocalFolderContributingToPortfolio() -> flowOf { true }
        sequenceDataSource is LocalAlbum ->
            portfolioUseCase.observeIndividualPortfolioItems().map {
                it.map(PortfolioItems::id)::contains
            }
        else -> flowOf { false }
    }

    context(LightboxActionsContext)
    private fun addToPortfolioIconEnabled(): Boolean = when {
        isViewingLocalFolderContributingToPortfolio() -> false
        sequenceDataSource is LocalAlbum -> true
        else -> false
    }

    context(LightboxActionsContext)
    private fun isViewingLocalFolderContributingToPortfolio() =
        sequenceDataSource is LocalAlbum && sequenceDataSource.albumId in
                portfolioUseCase.getPublishedFolderIds()

    private val Flow<List<MediaCollection>>.toMediaItems get() = map { collections ->
        collections.flatMap {
            it.mediaItems
        }
    }

    private fun ShowMedia(
        mediaItemStates: List<SingleMediaItemState>,
        id: MediaId<*>,
    ): ShowMedia? {
        val index = mediaItemStates.indexOfFirst { it.id.matches(id) }
        return ShowMedia(mediaItemStates, index).takeIf { index >=0 }
    }

    context(LightboxActionsContext)
    private fun MediaItem.toSingleMediaItemState(
        isInPortfolio: (Long) -> Boolean,
        showAddToPortfolioIcon: Boolean,
        addToPortfolioEnabled: Boolean
    ) = id.toSingleMediaItemState(isFavourite, isInPortfolio, showAddToPortfolioIcon, addToPortfolioEnabled)

    context(LightboxActionsContext)
    private fun MediaId<*>.toSingleMediaItemState(
        isFavourite: Boolean = false,
        isInPortfolio: (Long) -> Boolean = { false },
        showAddToPortfolioIcon: Boolean = false,
        addToPortfolioEnabled: Boolean = false,
    ) = SingleMediaItemState(
        id = this,
        showFavouriteIcon = preferRemote is MediaId.Remote,
        showDeleteButton = sequenceDataSource.shouldShowDeleteButton,
        showEditIcon = shouldShowEditButton,
        showShareIcon = !isVideo,
        showUseAsIcon = !isVideo,
        showAddToPortfolioIcon = showAddToPortfolioIcon,
        addToPortfolioIconEnabled = addToPortfolioEnabled,
        inPortfolio = findLocals.any { isInPortfolio(it.value) },
        mediaItemSyncState = syncState.takeIf { sequenceDataSource.showMediaSyncState },
        isFavourite = isFavourite
    )

    private val MediaId<*>.shouldShowEditButton get() = !isVideo && findLocals.isNotEmpty()
}
