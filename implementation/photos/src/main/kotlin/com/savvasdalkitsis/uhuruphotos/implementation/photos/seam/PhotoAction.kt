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
package com.savvasdalkitsis.uhuruphotos.implementation.photos.seam

import com.savvasdalkitsis.uhuruphotos.api.map.model.LatLon
import com.savvasdalkitsis.uhuruphotos.api.people.view.state.Person
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.SinglePhotoState

sealed class PhotoAction {
    object ToggleUI : PhotoAction()
    object NavigateBack : PhotoAction()
    object Refresh : PhotoAction()
    object DismissErrorMessage : PhotoAction()
    object ShowInfo : PhotoAction()
    object HideInfo : PhotoAction()
    object AskForPhotoTrashing : PhotoAction()
    object AskForPhotoRestoration : PhotoAction()
    object DismissConfirmationDialogs : PhotoAction()
    object TrashPhoto : PhotoAction()
    object RestorePhoto : PhotoAction()
    object SharePhoto : PhotoAction()
    object UsePhotoAs : PhotoAction()

    data class DownloadOriginal(val photo: SinglePhotoState) : PhotoAction()
    data class FullImageLoaded(val photo: SinglePhotoState) : PhotoAction()
    data class ClickedOnMap(val gps: LatLon) : PhotoAction()
    data class LoadPhoto(
        val id: String,
        val isVideo: Boolean,
        val datasource: PhotoSequenceDataSource
    ) : PhotoAction()
    data class SetFavourite(val favourite: Boolean) : PhotoAction()
    data class ClickedOnGps(val gps: LatLon) : PhotoAction()
    data class PersonSelected(val person: Person) : PhotoAction()
    data class ChangedToPage(val page: Int) : PhotoAction()
    data class ClickedOnDetailsEntry(val text: String) : PhotoAction()
}
