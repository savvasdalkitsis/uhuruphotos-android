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
import com.github.michaelbull.result.onFailure
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.portfolio.PortfolioItems
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchTypeModel
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel.AutoAlbumModel
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel.FavouriteMediaModel
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel.FeedModel
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel.HiddenMediaModel
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel.LocalAlbumModel
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel.MemoryModel
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel.PersonResultsModel
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel.SearchResultsModel
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel.SingleItemModel
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel.TrashModel
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel.UndatedModel
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel.UserAlbumModel
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel.VideosModel
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.FinishedLoading
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.FinishedLoadingDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.LoadingDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ReceivedDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowErrorMessage
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowMedia
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowRestoreButton
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.MediaItemTypeState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemModel
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.onIO
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlinx.coroutines.flow.onStart

@OptIn(ExperimentalCoroutinesApi::class)
data class LoadMediaItem(
    val actionMediaId: MediaIdModel<*>,
    val actionMediaItemHash: MediaItemHashModel,
    val sequenceDataSource: LightboxSequenceDataSourceModel,
) : LightboxAction() {

    override fun LightboxActionsContext.handle(
        state: LightboxState,
    ) = merge(
        flow {
            currentMediaId.emit(actionMediaId)
            emit(ShowMedia(listOf(actionMediaId.toSingleMediaItemState(mediaHash = actionMediaItemHash)), 0))

            if (sequenceDataSource == TrashModel) {
                mediaItemTypeState = MediaItemTypeState.TRASHED
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
                media.find(id)?.let { (index, item) ->
                    send(ShowMedia(media, index))
                    send(Loading)
                    send(LoadingDetails(id))
                    lightboxUseCase.observeLightboxItemDetails(item.mediaHash).map { details ->
                        ReceivedDetails(id, details, serverUrl)
                    }.onEach {
                        send(FinishedLoading)
                        send(FinishedLoadingDetails(id))
                    }.onStart {
                        onIO {
                            lightboxUseCase.refreshMediaDetails(id, item.mediaHash).onFailure {
                                send(ShowErrorMessage(string.error_loading_photo_details))
                            }
                        }
                    }.collect(this::send)
                }
            }
        }
    )

    private fun LightboxActionsContext.observeMediaSequence(): Flow<List<SingleMediaItemState>> =
        combine(
            observeMediaItemsSequence(),
            isInPortfolio(),
        ) { mediaItems, isInPortfolio ->
            val addToPortfolioEnabled = addToPortfolioIconEnabled()
            val showAddToPortfolioIcon = sequenceDataSource is LocalAlbumModel
                    && sequenceDataSource.albumId != localMediaUseCase.getDefaultFolderId()
            mediaItems.map {
                it.toSingleMediaItemState(isInPortfolio, showAddToPortfolioIcon, addToPortfolioEnabled)
            }
        }

    private fun LightboxActionsContext.observeMediaItemsSequence(): Flow<List<MediaItemModel>> = when (sequenceDataSource) {
        SingleItemModel -> emptyFlow()
        FeedModel -> feedUseCase.observeFeed(FeedFetchTypeModel.ONLY_WITH_DATES, loadSmallInitialChunk = false).toMediaItems
        is MemoryModel -> memoriesUseCase.observeMemories(loadSmallInitialChunk = false).map { collections ->
            collections.find { it.yearsAgo == sequenceDataSource.yearsAgo }?.mediaCollection?.mediaItems
            ?: emptyList()
        }
        is SearchResultsModel -> searchUseCase.searchFor(sequenceDataSource.query)
            .mapNotNull { it.getOr(null) }.toMediaItems
        is PersonResultsModel -> personUseCase.observePersonMedia(sequenceDataSource.personId).toMediaItems
        is AutoAlbumModel -> autoAlbumUseCase.observeAutoAlbum(sequenceDataSource.albumId).toMediaItems
        is UserAlbumModel -> userAlbumUseCase.observeUserAlbum(sequenceDataSource.albumId)
            .map { it.mediaCollections }.toMediaItems
        is LocalAlbumModel -> localAlbumUseCase.observeLocalAlbum(sequenceDataSource.albumId)
                .map { it.second }.toMediaItems
        FavouriteMediaModel -> mediaUseCase.observeFavouriteMedia().map { it.getOr(emptyList()) }
        HiddenMediaModel -> mediaUseCase.observeHiddenMedia().map { it.getOr(emptyList()) }
        TrashModel -> trashUseCase.observeTrashAlbums().toMediaItems
        VideosModel -> feedUseCase.observeFeed(FeedFetchTypeModel.VIDEOS).toMediaItems
        UndatedModel -> feedUseCase.observeFeed(FeedFetchTypeModel.ONLY_WITHOUT_DATES).toMediaItems
    }

    private fun LightboxActionsContext.isInPortfolio(): Flow<(Long) -> Boolean> = when {
        isViewingLocalFolderContributingToPortfolio() -> flowOf { true }
        sequenceDataSource is LocalAlbumModel ->
            portfolioUseCase.observeIndividualPortfolioItems().map {
                it.map(PortfolioItems::id)::contains
            }
        else -> flowOf { false }
    }

    private fun LightboxActionsContext.addToPortfolioIconEnabled(): Boolean = when {
        isViewingLocalFolderContributingToPortfolio() -> false
        sequenceDataSource is LocalAlbumModel -> true
        else -> false
    }

    private fun LightboxActionsContext.isViewingLocalFolderContributingToPortfolio() =
        sequenceDataSource is LocalAlbumModel && sequenceDataSource.albumId in
                portfolioUseCase.getPublishedFolderIds()

    private val Flow<List<MediaCollectionModel>>.toMediaItems get() = map { collections ->
        collections.flatMap {
            it.mediaItems
        }
    }

    private fun MediaItemModel.toSingleMediaItemState(
        isInPortfolio: (Long) -> Boolean,
        showAddToPortfolioIcon: Boolean,
        addToPortfolioEnabled: Boolean
    ) = id.toSingleMediaItemState(
        isFavourite = isFavourite,
        isInPortfolio = isInPortfolio,
        showAddToPortfolioIcon = showAddToPortfolioIcon,
        addToPortfolioEnabled = addToPortfolioEnabled,
        mediaHash = mediaHash
    )

    private fun MediaIdModel<*>.toSingleMediaItemState(
        isFavourite: Boolean = false,
        isInPortfolio: (Long) -> Boolean = { false },
        showAddToPortfolioIcon: Boolean = false,
        addToPortfolioEnabled: Boolean = false,
        mediaHash: MediaItemHashModel,
    ) = SingleMediaItemState(
        id = this,
        showFavouriteIcon = preferRemote is MediaIdModel.RemoteIdModel,
        showDeleteButton = sequenceDataSource.shouldShowDeleteButton,
        showEditIcon = shouldShowEditButton,
        showShareIcon = !isVideo,
        showUseAsIcon = !isVideo,
        showAddToPortfolioIcon = showAddToPortfolioIcon,
        addToPortfolioIconEnabled = addToPortfolioEnabled,
        inPortfolio = findLocals.any { isInPortfolio(it.value) },
        mediaItemSyncState = syncState.takeIf { sequenceDataSource.showMediaSyncState },
        isFavourite = isFavourite,
        mediaHash = mediaHash,
    )

    private val MediaIdModel<*>.shouldShowEditButton get() = !isVideo && findLocals.isNotEmpty()
}
