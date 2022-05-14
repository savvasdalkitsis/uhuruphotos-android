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
package com.savvasdalkitsis.uhuruphotos.photos.mvflow

import androidx.annotation.StringRes
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoDetails
import com.savvasdalkitsis.uhuruphotos.people.api.view.state.Person

sealed class PhotoMutation {
    object HideUI : PhotoMutation()
    object ShowUI : PhotoMutation()
    object DismissErrorMessage : PhotoMutation()
    data class ShowErrorMessage(@StringRes val message: Int) : PhotoMutation()
    object FinishedLoading : PhotoMutation()
    object Loading : PhotoMutation()
    object ShowInfo : PhotoMutation()
    object HideInfo : PhotoMutation()
    object ShowDeletionConfirmationDialog : PhotoMutation()
    object HideDeletionConfirmationDialog : PhotoMutation()
    object ShowShareIcon : PhotoMutation()
    data class ReceivedUrl(val id: String, val lowResUrl: String, val fullResUrl: String) : PhotoMutation()
    data class ReceivedDetails(val details: PhotoDetails, val peopleInPhoto: List<Person>) : PhotoMutation()
}
