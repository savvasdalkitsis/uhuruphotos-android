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

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.StoragePermissionRequest
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.Person
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon

sealed class LightboxAction {
    object ToggleUI : LightboxAction()
    object NavigateBack : LightboxAction()
    object Refresh : LightboxAction()
    object DismissErrorMessage : LightboxAction()
    object ShowInfo : LightboxAction()
    object HideInfo : LightboxAction()
    object AskForMediaItemTrashing : LightboxAction()
    object AskForMediaItemRestoration : LightboxAction()
    object DismissConfirmationDialogs : LightboxAction()
    object TrashMediaItem : LightboxAction()
    object RestoreMediaItem : LightboxAction()
    object DeleteLocalKeepRemoteMediaItem : LightboxAction()
    object ShareMediaItem : LightboxAction()
    object UseMediaItemAs : LightboxAction()

    data class DownloadOriginal(val mediaItemState: SingleMediaItemState) : LightboxAction()
    data class FullMediaDataLoaded(val mediaItemState: SingleMediaItemState) : LightboxAction()
    data class ClickedOnMap(val gps: LatLon) : LightboxAction()
    data class LoadMediaItem(
        val id: MediaId<*>,
        val isVideo: Boolean,
        val sequenceDataSource: LightboxSequenceDataSource,
        val showMediaSyncState: Boolean,
    ) : LightboxAction()
    data class SetFavourite(val favourite: Boolean) : LightboxAction()
    data class ClickedOnGps(val gps: LatLon) : LightboxAction()
    data class PersonSelected(val person: Person) : LightboxAction()
    data class ChangedToPage(val page: Int) : LightboxAction()
    data class ClickedOnDetailsEntry(val text: String) : LightboxAction()
    data class AllowStorageManagement(val request: StoragePermissionRequest) : LightboxAction()
}
