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
package com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state

import androidx.annotation.StringRes
import com.savvasdalkitsis.uhuruphotos.api.map.model.LatLon
import com.savvasdalkitsis.uhuruphotos.api.people.view.state.Person
import com.savvasdalkitsis.uhuruphotos.implementation.photos.usecase.PhotoMetadata

data class PhotoState(
    val currentIndex: Int = 0,
    val photos: List<SinglePhotoState> = emptyList(),
    val isLoading: Boolean = false,
    @StringRes val errorMessage: Int? = null,
    val showUI: Boolean = true,
    val showRefresh: Boolean = false,
    val showInfoButton: Boolean = false,
    val showPhotoDeleteConfirmationDialog: Boolean = false,
    val showPhotoTrashingConfirmationDialog: Boolean = false,
    val showPhotoRestorationConfirmationDialog: Boolean = false,
    val infoSheetHidden: Boolean = true,
    val showRestoreButton: Boolean = false,
) {
    val currentPhoto: SinglePhotoState get() = photos[currentIndex]
}

data class SinglePhotoState(
    val id: String = "",
    val fullResUrl: String = "",
    val lowResUrl: String = "",
    val isFavourite: Boolean? = null,
    val dateAndTime: String = "",
    val location: String = "",
    val gps: LatLon? = null,
    val isVideo: Boolean = false,
    val showShareIcon: Boolean = false,
    val originalFileIconState: OriginalFileIconState = OriginalFileIconState.HIDDEN,
    val peopleInPhoto: List<Person> = emptyList(),
    val metadata: PhotoMetadata? = null,
    val path: String? = null,
)

enum class OriginalFileIconState {
    HIDDEN, IDLE, IN_PROGRESS, ERROR
}