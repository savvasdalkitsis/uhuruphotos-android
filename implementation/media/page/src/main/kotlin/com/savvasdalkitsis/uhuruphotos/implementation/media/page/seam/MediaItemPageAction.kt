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
package com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam

import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaId
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.api.people.view.state.Person
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.view.state.SingleMediaItemState

sealed class MediaItemPageAction {
    object ToggleUI : MediaItemPageAction()
    object NavigateBack : MediaItemPageAction()
    object Refresh : MediaItemPageAction()
    object DismissErrorMessage : MediaItemPageAction()
    object ShowInfo : MediaItemPageAction()
    object HideInfo : MediaItemPageAction()
    object AskForMediaItemTrashing : MediaItemPageAction()
    object AskForMediaItemRestoration : MediaItemPageAction()
    object DismissConfirmationDialogs : MediaItemPageAction()
    object TrashMediaItem : MediaItemPageAction()
    object RestoreMediaItem : MediaItemPageAction()
    object ShareMediaItem : MediaItemPageAction()
    object UseMediaItemAs : MediaItemPageAction()

    data class DownloadOriginal(val photo: SingleMediaItemState) : MediaItemPageAction()
    data class FullMediaDataLoaded(val photo: SingleMediaItemState) : MediaItemPageAction()
    data class ClickedOnMap(val gps: LatLon) : MediaItemPageAction()
    data class LoadMediaItem(
        val id: MediaId<*>,
        val isVideo: Boolean,
        val sequenceDataSource: MediaSequenceDataSource
    ) : MediaItemPageAction()
    data class SetFavourite(val favourite: Boolean) : MediaItemPageAction()
    data class ClickedOnGps(val gps: LatLon) : MediaItemPageAction()
    data class PersonSelected(val person: Person) : MediaItemPageAction()
    data class ChangedToPage(val page: Int) : MediaItemPageAction()
    data class ClickedOnDetailsEntry(val text: String) : MediaItemPageAction()
}
