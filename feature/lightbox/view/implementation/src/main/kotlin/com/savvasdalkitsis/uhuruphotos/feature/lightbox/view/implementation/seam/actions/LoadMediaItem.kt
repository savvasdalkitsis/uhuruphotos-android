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
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.*
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffect
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.MediaItemType
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

data class LoadMediaItem(
    val id: MediaId<*>,
    val isVideo: Boolean,
    val sequenceDataSource: LightboxSequenceDataSource,
    val showMediaSyncState: Boolean,
) : LightboxAction() {

    context(LightboxActionsContext) override fun handle(
        state: LightboxState,
        effect: EffectHandler<LightboxEffect>,
    ) = flow {
        val dataSource = sequenceDataSource
        if (dataSource == Trash) {
            mediaItemType = MediaItemType.TRASHED
            emit(LightboxMutation.ShowRestoreButton)
        }
        with(mediaUseCase) {
            emit(LightboxMutation.ShowSingleMediaItem(
                SingleMediaItemState(
                    id = id,
                    lowResUrl = id.toThumbnailUriFromId(isVideo),
                    fullResUrl = id.toFullSizeUriFromId(isVideo),
                    showFavouriteIcon = id.preferRemote is MediaId.Remote,
                    showDeleteButton = true,
                    mediaItemSyncState = id.syncState.takeIf { showMediaSyncState }
                )
            ))
            when (dataSource) {
                Single -> loadPhotoDetails(
                    photoId = id,
                    isVideo = isVideo
                )
                Feed -> loadCollections(
                    feedUseCase.getFeed()
                )
                is SearchResults -> loadCollections(
                    searchUseCase.searchResultsFor(dataSource.query),
                )
                is PersonResults -> loadCollections(
                    personUseCase.getPersonMedia(dataSource.personId),
                )
                is AutoAlbum -> loadCollections(
                    autoAlbumUseCase.getAutoAlbum(dataSource.albumId),
                )
                is UserAlbum -> loadCollections(
                    userAlbumUseCase.getUserAlbum(dataSource.albumId).mediaCollections,
                )
                is LocalAlbum -> loadCollections(
                    localAlbumUseCase.getLocalAlbum(dataSource.albumId),
                )
                FavouriteMedia -> loadResult(
                    mediaUseCase.getFavouriteMedia(),
                )
                HiddenMedia -> loadResult(
                    mediaUseCase.getHiddenMedia(),
                )
                Trash -> loadCollections(
                    trashUseCase.getTrash(),
                )
            }
        }
    }


    context(MediaUseCase, LightboxActionsContext)
    private suspend fun FlowCollector<LightboxMutation>.loadCollections(
        collections: List<MediaCollection>
    ) = loadPhotos(collections.flatMap { it.mediaItems })

    context(MediaUseCase, LightboxActionsContext)
    private suspend fun FlowCollector<LightboxMutation>.loadResult(
        mediaItem: Result<List<MediaItem>>,
    ) = when (val items = mediaItem.getOrNull()) {
        null -> loadPhotoDetails(id)
        else -> loadPhotos(items)
    }

    context(MediaUseCase, LightboxActionsContext)
    private suspend fun FlowCollector<LightboxMutation>.loadPhotos(
        mediaItems: List<MediaItem>
    ) {
        val photoStates = mediaItems.map { photo ->
            SingleMediaItemState(
                id = photo.id,
                lowResUrl = photo.thumbnailUri ?: photo.id.toThumbnailUriFromId(isVideo),
                fullResUrl = photo.fullResUri ?: photo.id.toFullSizeUriFromId(isVideo),
                isFavourite = photo.isFavourite,
                isVideo = photo.isVideo,
                showFavouriteIcon = photo.id.preferRemote is MediaId.Remote,
                showDeleteButton = true,
                mediaItemSyncState = photo.syncState.takeIf { showMediaSyncState }
            )
        }
        val index = photoStates.indexOfFirst { it.id == id }
        emit(LightboxMutation.ShowMultipleMedia(photoStates, index))
        loadPhotoDetails(
            photoId = id,
            isVideo = isVideo
        )
    }
}
