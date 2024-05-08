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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.api.usecase.AutoAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.api.usecase.UserAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.api.navigation.EditNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.domain.api.usecase.LightboxUseCase
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxDeletionCategory.FULLY_SYNCED_ITEM
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxDeletionCategory.LOCAL_ONLY_ITEM
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxDeletionCategory.REMOTE_ITEM
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxDeletionCategory.REMOTE_ITEM_TRASHED
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.AskForPermissions
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.FinishedLoading
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.HideAllConfirmationDialogs
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.LoadingDetails
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
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaDeletionRequest
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItemDeletion.Error
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItemDeletion.RequiresPermissions
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItemDeletion.Success
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MissingPermissionsException
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaDeletionUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.memories.domain.api.usecase.MemoriesUseCase
import com.savvasdalkitsis.uhuruphotos.feature.person.domain.api.usecase.PersonUseCase
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.api.usecase.PortfolioUseCase
import com.savvasdalkitsis.uhuruphotos.feature.search.domain.api.usecase.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.feature.trash.domain.api.usecase.TrashUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import com.savvasdalkitsis.uhuruphotos.foundation.share.api.usecase.ShareUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.usecase.ToasterUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.usecase.UiUseCase
import com.savvasdalktsis.uhuruphotos.feature.download.domain.api.usecase.DownloadUseCase
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import org.joda.time.format.DateTimeFormatter
import kotlin.random.Random

class LightboxActionsContext(
    val mediaUseCase: MediaUseCase,
    val downloadUseCase: DownloadUseCase,
    val uploadUseCase: UploadUseCase,
    val personUseCase: PersonUseCase,
    val feedUseCase: FeedUseCase,
    val memoriesUseCase: MemoriesUseCase,
    val searchUseCase: SearchUseCase,
    val localAlbumUseCase: LocalAlbumUseCase,
    val metadataUseCase: MetadataUseCase,
    val userAlbumUseCase: UserAlbumUseCase,
    val autoAlbumUseCase: AutoAlbumUseCase,
    val trashUseCase: TrashUseCase,
    val remoteMediaUseCase: RemoteMediaUseCase,
    val portfolioUseCase: PortfolioUseCase,
    val localMediaUseCase: LocalMediaUseCase,
    val displayingDateTimeFormat: DateTimeFormatter,
    val context: Context,
    val packageManager: PackageManager,
    val uiUseCase: UiUseCase,
    val shareUseCase: ShareUseCase,
    val toaster: ToasterUseCase,
    val navigator: Navigator,
    private val clipboardManager: ClipboardManager,
    private val localMediaDeletionUseCase: LocalMediaDeletionUseCase,
    val serverUseCase: ServerUseCase,
    val lightboxUseCase: LightboxUseCase,
) {

    var mediaItemType = MediaItemType.default
    val currentMediaId = MutableSharedFlow<MediaId<*>>(1)

    fun deletionCategory(item: SingleMediaItemState) = when {
        mediaItemType == TRASHED -> REMOTE_ITEM_TRASHED
        item.id.isBothRemoteAndLocal -> FULLY_SYNCED_ITEM
        item.id.findLocals.isNotEmpty() -> LOCAL_ONLY_ITEM
        else -> REMOTE_ITEM
    }

    suspend fun FlowCollector<LightboxMutation>.deleteLocal(
        mediaItem: SingleMediaItemState
    ): SimpleResult {
        val result = localMediaDeletionUseCase.deleteLocalMediaItems(
            mediaItem.id.findLocals.map { id ->
                LocalMediaDeletionRequest(id.value, id.isVideo)
            }
        )
        return when(result) {
            is Error -> Err(result.e)
            is RequiresPermissions -> {
                emit(AskForPermissions(result.deniedPermissions))
                Err(MissingPermissionsException(result.deniedPermissions))
            }
            Success -> Ok(Unit)
        }
    }

    fun processAndRemoveMediaItem(
        state: LightboxState,
        process: suspend FlowCollector<LightboxMutation>.() -> SimpleResult,
    ) = processMediaItem(state, process) {
        emit(RemoveMediaItemFromSource(state.currentMediaItem.id))
    }

    fun processMediaItem(
        state: LightboxState,
        process: suspend FlowCollector<LightboxMutation>.() -> SimpleResult,
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
                    uiUseCase.setSystemBarsVisibility(true)
                    navigator.navigateUp()
                }
            }
    }

    suspend fun FlowCollector<LightboxMutation>.refreshMediaDetails(
        mediaId: MediaId<*>,
        media: List<SingleMediaItemState>,
    ) {
        media.find(mediaId)?.let { (_, item) ->
            emit(Loading)
            emit(LoadingDetails(mediaId))
            lightboxUseCase.refreshMediaDetails(mediaId, item.mediaHash).onFailure {
                emit(ShowErrorMessage(strings.error_loading_photo_details))
            }
        }
    }

    fun List<SingleMediaItemState>.find(id: MediaId<*>): Pair<Int, SingleMediaItemState>? {
        val index = indexOfFirst { it.id.matches(id) }
        return if (index >= 0) {
            index to get(index)
        } else {
            null
        }
    }

    internal fun LightboxActionsContext.cropLocal(
        state: LightboxState
    ) {
        fun SingleMediaItemState.fileName() = details.localPaths.firstOrNull()?.substringAfterLast("/")
            ?: "PHOTO_${Random.nextInt()}"
        state.currentMediaItem.id.findLocals.firstOrNull()?.let { media ->
            navigator.navigateTo(EditNavigationRoute(
                uri = Uri.parse(media.contentUri).toString(),
                fileName = state.currentMediaItem.fileName(),
                timestamp = try {
                    displayingDateTimeFormat.parseDateTime(state.currentMediaItem.details.formattedDateTime).millis
                } catch (e: Exception) {
                    log(e)
                    null
                },
            ))
        }
    }

    fun copyToClipBoard(text: String) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", text))
    }
}
