package com.savvasdalkitsis.uhuruphotos.person.viewmodel

import androidx.compose.ui.geometry.Offset
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.photos.model.Photo

sealed class PersonAction {
    data class LoadPerson(val id: Int) : PersonAction()
    data class SelectedPhoto(val photo: Photo, val center: Offset, val scale: Float) : PersonAction()
    data class ChangeDisplay(val display: FeedDisplay) : PersonAction()
    object NavigateBack : PersonAction()
}
