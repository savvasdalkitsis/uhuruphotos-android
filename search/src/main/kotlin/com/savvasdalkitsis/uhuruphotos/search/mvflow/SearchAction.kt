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
package com.savvasdalkitsis.uhuruphotos.search.mvflow

import androidx.compose.ui.geometry.Offset
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.people.api.view.state.Person
import com.savvasdalkitsis.uhuruphotos.photos.model.Photo

sealed class SearchAction {

    object Initialise : SearchAction()
    object ClearSearch : SearchAction()
    object UserBadgePressed : SearchAction()
    object DismissAccountOverview : SearchAction()
    object AskToLogOut : SearchAction()
    object DismissLogOutDialog : SearchAction()
    object LogOut : SearchAction()
    object EditServer : SearchAction()
    object SettingsClick : SearchAction()
    object SendLogsClick : SearchAction()
    object ViewAllPeopleSelected : SearchAction()
    object LoadHeatMap : SearchAction()

    data class ChangeQuery(val query: String) : SearchAction()
    data class SearchFor(val query: String) : SearchAction()
    data class ChangeFocus(val focused: Boolean) : SearchAction()
    data class SelectedPhoto(val photo: Photo, val center: Offset, val scale: Float) : SearchAction()
    data class ChangeDisplay(val display: FeedDisplay) : SearchAction()
    data class PersonSelected(val person: Person) : SearchAction()
}