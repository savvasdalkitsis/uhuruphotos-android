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
        val action = this@LoadMediaItem
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
                    feedUseCase.getFeed(),
                    action
                )
                is SearchResults -> loadCollections(
                    searchUseCase.searchResultsFor(dataSource.query),
                    action,
                )
                is PersonResults -> loadCollections(
                    personUseCase.getPersonMedia(dataSource.personId),
                    action,
                )
                is AutoAlbum -> loadCollections(
                    autoAlbumUseCase.getAutoAlbum(dataSource.albumId),
                    action,
                )
                is UserAlbum -> loadCollections(
                    userAlbumUseCase.getUserAlbum(dataSource.albumId).mediaCollections,
                    action,
                )
                is LocalAlbum -> loadCollections(
                    localAlbumUseCase.getLocalAlbum(dataSource.albumId),
                    action,
                )
                FavouriteMedia -> loadResult(
                    mediaUseCase.getFavouriteMedia(),
                    action,
                )
                HiddenMedia -> loadResult(
                    mediaUseCase.getHiddenMedia(),
                    action,
                )
                Trash -> loadCollections(
                    trashUseCase.getTrash(),
                    action,
                )
            }
        }
    }


    context(MediaUseCase, LightboxActionsContext)
    private suspend fun FlowCollector<LightboxMutation>.loadCollections(
        collections: List<MediaCollection>,
        action: LoadMediaItem
    ) = loadPhotos(collections.flatMap { it.mediaItems }, action)

    context(MediaUseCase, LightboxActionsContext)
    private suspend fun FlowCollector<LightboxMutation>.loadResult(
        mediaItem: Result<List<MediaItem>>,
        action: LoadMediaItem,
    ) = when (val items = mediaItem.getOrNull()) {
        null -> loadPhotoDetails(action.id)
        else -> loadPhotos(items, action)
    }

    context(MediaUseCase, LightboxActionsContext)
    private suspend fun FlowCollector<LightboxMutation>.loadPhotos(
        mediaItems: List<MediaItem>,
        action: LoadMediaItem
    ) {
        val photoStates = mediaItems.map { photo ->
            SingleMediaItemState(
                id = photo.id,
                lowResUrl = photo.thumbnailUri ?: photo.id.toThumbnailUriFromId(action.isVideo),
                fullResUrl = photo.fullResUri ?: photo.id.toFullSizeUriFromId(action.isVideo),
                isFavourite = photo.isFavourite,
                isVideo = photo.isVideo,
                showFavouriteIcon = photo.id.preferRemote is MediaId.Remote,
                showDeleteButton = true,
                mediaItemSyncState = photo.syncState.takeIf { action.showMediaSyncState }
            )
        }
        val index = photoStates.indexOfFirst { it.id == action.id }
        emit(LightboxMutation.ShowMultipleMedia(photoStates, index))
        loadPhotoDetails(
            photoId = action.id,
            isVideo = action.isVideo
        )
    }
}