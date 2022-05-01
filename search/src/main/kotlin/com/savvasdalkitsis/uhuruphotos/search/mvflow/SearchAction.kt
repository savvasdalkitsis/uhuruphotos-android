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
    object ViewAllPeopleSelected : SearchAction()

    data class ChangeQuery(val query: String) : SearchAction()
    data class SearchFor(val query: String) : SearchAction()
    data class ChangeFocus(val focused: Boolean) : SearchAction()
    data class SelectedPhoto(val photo: Photo, val center: Offset, val scale: Float) : SearchAction()
    data class ChangeDisplay(val display: FeedDisplay) : SearchAction()
    data class PersonSelected(val person: Person) : SearchAction()
}