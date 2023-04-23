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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.api.usecase.AutoAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.api.usecase.UserAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxDeletionCategory.FULLY_SYNCED_ITEM
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxDeletionCategory.LOCAL_ONLY_ITEM
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxDeletionCategory.REMOTE_ITEM
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxDeletionCategory.REMOTE_ITEM_TRASHED
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.FinishedLoading
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.FinishedLoadingDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.HideAllConfirmationDialogs
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.LoadingDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ReceivedDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.RemoveMediaItemFromSource
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowErrorMessage
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.MediaItemType
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.MediaItemType.TRASHED
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.feature.local.domain.api.usecase.LocalAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MetadataUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItemDeletion
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MissingPermissionsException
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.person.domain.api.usecase.PersonUseCase
import com.savvasdalkitsis.uhuruphotos.feature.search.domain.api.usecase.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.feature.trash.domain.api.usecase.TrashUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.request.ActivityRequestLauncher
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ActivityRetainedScoped
internal class LightboxActionsContext @Inject constructor(
    val mediaUseCase: MediaUseCase,
    val personUseCase: PersonUseCase,
    val feedUseCase: FeedUseCase,
    val searchUseCase: SearchUseCase,
    val localAlbumUseCase: LocalAlbumUseCase,
    val metadataUseCase: MetadataUseCase,
    val userAlbumUseCase: UserAlbumUseCase,
    val autoAlbumUseCase: AutoAlbumUseCase,
    val trashUseCase: TrashUseCase,
    private val localMediaUseCase: LocalMediaUseCase,
    private val activityRequestLauncher: ActivityRequestLauncher,
) {

    var mediaItemType = MediaItemType.default

    fun deletionCategory(item: SingleMediaItemState) = when {
        mediaItemType == TRASHED -> REMOTE_ITEM_TRASHED
        item.id.isBothRemoteAndLocal -> FULLY_SYNCED_ITEM
        item.id.findLocal != null -> LOCAL_ONLY_ITEM
        else -> REMOTE_ITEM
    }

    suspend fun FlowCollector<LightboxMutation>.deleteLocal(
        mediaItem: SingleMediaItemState
    ): Result<Unit> {
        val id = mediaItem.id.findLocal!!.value
        val result = localMediaUseCase.deleteLocalMediaItem(
            id,
            mediaItem.isVideo
        )
        return when (result) {
            is LocalMediaItemDeletion.Error -> Result.failure(result.e ?: UnknownError())
            is LocalMediaItemDeletion.RequiresPermissions -> {
                emit(LightboxMutation.AskForPermissions(result.deniedPermissions))
                Result.failure(MissingPermissionsException(result.deniedPermissions))
            }

            is LocalMediaItemDeletion.NeedsSystemApproval ->
                activityRequestLauncher.performRequest(
                    "deleteLocalMedia:${id}",
                    result.request,
                ) {
                    // deleting again cause on R sometimes the file is not deleted at the same flow
                    // we give permission in
                    localMediaUseCase.deleteLocalMediaItem(
                        id,
                        mediaItem.isVideo
                    )
                }
            LocalMediaItemDeletion.Success -> Result.success(Unit)
        }
    }

    fun processAndRemovePhoto(
        state: LightboxState,
        effect: EffectHandler<LightboxEffect>,
        process: suspend FlowCollector<LightboxMutation>.() -> Result<Unit>,
    ) = processPhoto(state, effect, process) {
        emit(RemoveMediaItemFromSource(state.currentMediaItem.id))
    }

    fun processPhoto(
        state: LightboxState,
        effect: EffectHandler<LightboxEffect>,
        process: suspend FlowCollector<LightboxMutation>.() -> Result<Unit>,
        postProcessAction: suspend FlowCollector<LightboxMutation>.() -> Unit,
    ) = flow {
        emit(Loading)
        emit(HideAllConfirmationDialogs)
        val result = process()
        emit(FinishedLoading)
        result
            .onFailure {
                log(it)
            }
            .onSuccess {
                postProcessAction()
                if (state.media.size == 1) {
                    effect.handleEffect(LightboxEffect.NavigateBack)
                }
            }
    }

    suspend fun FlowCollector<LightboxMutation>.loadPhotoDetails(
        photoId: MediaId<*>,
        isVideo: Boolean = false,
        refresh: Boolean = false,
    ) {
        emit(Loading)
        emit(LoadingDetails(photoId))
        if (refresh) {
            mediaUseCase.refreshDetailsNow(photoId, isVideo)
        } else {
            mediaUseCase.refreshDetailsNowIfMissing(photoId, isVideo)
        }.onFailure {
            emit(ShowErrorMessage(string.error_loading_photo_details))
        }
        when (val details = mediaUseCase.getMediaItemDetails(photoId)) {
            null -> emit(ShowErrorMessage(string.error_loading_photo_details))
            else -> emit(ReceivedDetails(photoId, details))
        }
        emit(FinishedLoading)
        emit(FinishedLoadingDetails(photoId))
    }
}
