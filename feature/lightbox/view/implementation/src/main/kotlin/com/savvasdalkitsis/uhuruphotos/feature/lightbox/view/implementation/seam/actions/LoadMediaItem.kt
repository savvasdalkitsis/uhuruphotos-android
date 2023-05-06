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
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.UserAlbum
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffect
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowMedia
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowRestoreButton
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.MediaItemType
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

data class LoadMediaItem(
    val actionMediaId: MediaId<*>,
    val sequenceDataSource: LightboxSequenceDataSource,
    val showMediaSyncState: Boolean,
) : LightboxAction() {

    context(LightboxActionsContext) override fun handle(
        state: LightboxState,
        effect: EffectHandler<LightboxEffect>,
    ) = flow {
        if (sequenceDataSource == Trash) {
            mediaItemType = MediaItemType.TRASHED
            emit(ShowRestoreButton)
        }

        showMedia(listOf(actionMediaId.toSingleMediaItemState()))

        showMedia(loadMediaFromSequenceToShow())

        loadMediaDetails(actionMediaId)
    }

    context(LightboxActionsContext)
    private suspend fun loadMediaFromSequenceToShow() = when (sequenceDataSource) {
        Single -> emptyList()
        Feed -> feedUseCase.getFeed().toMediaItems
        is Memory -> memoriesUseCase.getMemories()
            .find { it.yearsAgo == sequenceDataSource.yearsAgo }?.mediaCollection?.mediaItems
            ?: emptyList()
        is SearchResults -> searchUseCase.searchResultsFor(sequenceDataSource.query).toMediaItems
        is PersonResults -> personUseCase.getPersonMedia(sequenceDataSource.personId).toMediaItems
        is AutoAlbum -> autoAlbumUseCase.getAutoAlbum(sequenceDataSource.albumId).toMediaItems
        is UserAlbum -> userAlbumUseCase.getUserAlbum(sequenceDataSource.albumId).mediaCollections.toMediaItems
        is LocalAlbum -> localAlbumUseCase.getLocalAlbum(sequenceDataSource.albumId).toMediaItems
        FavouriteMedia -> mediaUseCase.getFavouriteMedia().getOrDefault(emptyList())
        HiddenMedia -> mediaUseCase.getHiddenMedia().getOrDefault(emptyList())
        Trash -> trashUseCase.getTrash().toMediaItems
    }.map {
        it.toSingleMediaItemState()
    }

    private val List<MediaCollection>.toMediaItems get() = flatMap { it.mediaItems }

    context(LightboxActionsContext)
    private suspend fun FlowCollector<LightboxMutation>.showMedia(
        mediaItemStates: List<SingleMediaItemState>,
    ) {
        if (mediaItemStates.isNotEmpty()) {
            val index = mediaItemStates.indexOfFirst { it.id == actionMediaId }
            emit(ShowMedia(mediaItemStates, index))
        }
    }

    private val shouldShowDeleteButton =
        sequenceDataSource is Feed ||
        sequenceDataSource is Memory ||
        sequenceDataSource is LocalAlbum

    context(LightboxActionsContext)
    private fun MediaItem.toSingleMediaItemState() = with(mediaUseCase) {
        SingleMediaItemState(
            id = id,
            fullResUrl = fullResUri ?: id.toFullSizeUriFromId(),
            lowResUrl = thumbnailUri ?: id.toThumbnailUriFromId(),
            isFavourite = isFavourite,
            showFavouriteIcon = id.preferRemote is MediaId.Remote,
            showDeleteButton = shouldShowDeleteButton,
            mediaItemSyncState = syncState.takeIf { showMediaSyncState }
        )
    }
    context(LightboxActionsContext)
    private fun MediaId<*>.toSingleMediaItemState() = with(mediaUseCase) {
        SingleMediaItemState(
            id = this@toSingleMediaItemState,
            fullResUrl = toFullSizeUriFromId(),
            lowResUrl = toThumbnailUriFromId(),
            showFavouriteIcon = false,
            showDeleteButton = shouldShowDeleteButton,
            mediaItemSyncState = syncState.takeIf { showMediaSyncState }
        )
    }
}