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
package com.savvasdalkitsis.uhuruphotos.photos.view.state

import androidx.annotation.StringRes
import com.google.android.gms.maps.model.LatLng
import com.savvasdalkitsis.uhuruphotos.people.api.view.state.Person

data class PhotoState(
    val id: String = "",
    val isLoading: Boolean = false,
    val fullResUrl: String = "",
    val lowResUrl: String = "",
    @StringRes val errorMessage: Int? = null,
    val showUI: Boolean = true,
    val showRefresh: Boolean = false,
    val showInfoButton: Boolean = false,
    val showPhotoDeletionConfirmationDialog: Boolean = false,
    val showShareIcon: Boolean = false,
    val isFavourite: Boolean? = null,
    val infoSheetHidden: Boolean = true,
    val dateAndTime: String = "",
    val location: String = "",
    val gps: LatLng? = null,
    val isVideo: Boolean = false,
    val peopleInPhoto: List<Person> = emptyList(),
)