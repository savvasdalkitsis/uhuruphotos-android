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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.api.usecase.UserAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationLoginUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.avatar.domain.api.usecase.AvatarUseCase
import com.savvasdalkitsis.uhuruphotos.feature.battery.domain.api.usecase.BatteryUseCase
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.domain.api.usecase.UserAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.SelectionList
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.AskForPermissions
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.usecase.JobsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaDeletionRequestModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItemDeletion.Error
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItemDeletion.RequiresPermissions
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItemDeletion.Success
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaDeletionUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.memories.domain.api.usecase.MemoriesUseCase
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUIUseCase
import com.savvasdalkitsis.uhuruphotos.feature.sync.domain.api.usecase.SyncUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.WelcomeUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.share.api.usecase.ShareUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.usecase.ToasterUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.usecase.UiUseCase
import com.savvasdalktsis.uhuruphotos.feature.download.domain.api.usecase.DownloadUseCase
import kotlinx.coroutines.flow.FlowCollector
import javax.inject.Inject

class FeedActionsContext @Inject constructor(
    val avatarUseCase: AvatarUseCase,
    val downloadUseCase: DownloadUseCase,
    val uploadUseCase: UploadUseCase,
    val feedUseCase: FeedUseCase,
    val feedWorkScheduler: FeedWorkScheduler,
    val mediaUseCase: MediaUseCase,
    val jobsUseCase: JobsUseCase,
    val localMediaDeletionUseCase: LocalMediaDeletionUseCase,
    val selectionList: SelectionList,
    val settingsUIUseCase: SettingsUIUseCase,
    val memoriesUseCase: MemoriesUseCase,
    val localMediaWorkScheduler: LocalMediaWorkScheduler,
    val uiUseCase: UiUseCase,
    val shareUseCase: ShareUseCase,
    val toaster: ToasterUseCase,
    val navigator: Navigator,
    val welcomeUseCase: WelcomeUseCase,
    val syncUseCase: SyncUseCase,
    val authenticationLoginUseCase: AuthenticationLoginUseCase,
    val serverUseCase: ServerUseCase,
    val batteryUseCase: BatteryUseCase,
    val userAlbumsUseCase: UserAlbumsUseCase,
    val userAlbumUseCase: UserAlbumUseCase,
) {
    suspend fun CelState.deselect() {
        selectionList.deselect(mediaItem.id)
    }

    suspend fun CelState.select() {
        selectionList.select(mediaItem.id)
    }

    fun trashRemoteSelectedCels(state: FeedState) = state.selectedCels
        .mapNotNull { it.mediaItem.id.findRemote }
        .forEach {
            mediaUseCase.trashMediaItem(it)
        }

    suspend fun FlowCollector<FeedMutation>.deleteLocalSelectedCels(
        state: FeedState,
        onSuccess: suspend () -> Unit = {},
    ) {
        when (val result = localMediaDeletionUseCase.deleteLocalMediaItems(state.selectedCels
            .flatMap { it.mediaItem.id.findLocals }
            .map { LocalMediaDeletionRequestModel(it.value, it.isVideo) }
        )) {
            is Error -> toaster.show(R.string.error_deleting_media)
            is RequiresPermissions -> emit(AskForPermissions(result.deniedPermissions))
            Success -> onSuccess()
        }
        selectionList.clear()
    }
}
