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
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.*
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ReceivedDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowMedia
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowRestoreButton
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.effects.LightboxEffect
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.MediaItemType
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge

data class LoadMediaItem(
    val actionMediaId: MediaId<*>,
    val sequenceDataSource: LightboxSequenceDataSource,
    val showMediaSyncState: Boolean,
) : LightboxAction() {

    context(LightboxActionsContext) override fun handle(
        state: LightboxState,
        effect: EffectHandler<LightboxEffect>,
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
            currentMediaId,
        ) { media, id ->
            media to id
        }.flatMapLatest { (media, id) ->
            flow {
                ShowMedia(media, id)?.let {
                    emit(it)
                    loadMediaDetails(id)
                    emitAll(mediaUseCase.observeMediaItemDetails(id).map { details ->
                        ReceivedDetails(id, details)
                    })
                }
            }
        }
    )

    context(LightboxActionsContext)
    private fun observeMediaSequence(): Flow<List<SingleMediaItemState>> = when (sequenceDataSource) {
        Single -> emptyFlow()
        Feed -> feedUseCase.observeFeed(FeedFetchType.ONLY_WITH_DATES).toMediaItems
        is Memory -> memoriesUseCase.observeMemories().map { collections ->
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
    }.map { mediaItems ->
        mediaItems.map { it.toSingleMediaItemState() }
    }

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

    private val shouldShowDeleteButton =
        sequenceDataSource is Feed ||
        sequenceDataSource is Memory ||
        sequenceDataSource is LocalAlbum

    context(LightboxActionsContext)
    private fun MediaItem.toSingleMediaItemState() =
        id.toSingleMediaItemState(isFavourite)

    context(LightboxActionsContext)
    private fun MediaId<*>.toSingleMediaItemState(
        isFavourite: Boolean = false,
    ) =
        SingleMediaItemState(
            id = this,
            isFavourite = isFavourite,
            showFavouriteIcon = preferRemote is MediaId.Remote,
            showDeleteButton = shouldShowDeleteButton,
            showEditIcon = shouldShowEditButton,
            mediaItemSyncState = syncState.takeIf { showMediaSyncState }
        )

    private val MediaId<*>.shouldShowEditButton get() = !isVideo && findLocal != null
}
