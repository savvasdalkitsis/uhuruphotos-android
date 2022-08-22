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
package com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.api.model.ExhibitSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.Person
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon

sealed class ExhibitAction {
    object ToggleUI : ExhibitAction()
    object NavigateBack : ExhibitAction()
    object Refresh : ExhibitAction()
    object DismissErrorMessage : ExhibitAction()
    object ShowInfo : ExhibitAction()
    object HideInfo : ExhibitAction()
    object AskForMediaItemTrashing : ExhibitAction()
    object AskForMediaItemRestoration : ExhibitAction()
    object DismissConfirmationDialogs : ExhibitAction()
    object TrashMediaItem : ExhibitAction()
    object RestoreMediaItem : ExhibitAction()
    object ShareMediaItem : ExhibitAction()
    object UseMediaItemAs : ExhibitAction()

    data class DownloadOriginal(val photo: SingleMediaItemState) : ExhibitAction()
    data class FullMediaDataLoaded(val photo: SingleMediaItemState) : ExhibitAction()
    data class ClickedOnMap(val gps: LatLon) : ExhibitAction()
    data class LoadMediaItem(
        val id: MediaId<*>,
        val isVideo: Boolean,
        val sequenceDataSource: ExhibitSequenceDataSource
    ) : ExhibitAction()
    data class SetFavourite(val favourite: Boolean) : ExhibitAction()
    data class ClickedOnGps(val gps: LatLon) : ExhibitAction()
    data class PersonSelected(val person: Person) : ExhibitAction()
    data class ChangedToPage(val page: Int) : ExhibitAction()
    data class ClickedOnDetailsEntry(val text: String) : ExhibitAction()
}
