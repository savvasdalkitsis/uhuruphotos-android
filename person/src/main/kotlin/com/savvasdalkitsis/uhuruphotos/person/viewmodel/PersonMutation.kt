package com.savvasdalkitsis.uhuruphotos.person.viewmodel

import com.savvasdalkitsis.uhuruphotos.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.db.people.People
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplay

sealed class PersonMutation {
    object Loading : PersonMutation()
    data class ShowPersonPhotos(val albums: List<Album>) : PersonMutation()
    data class ShowPersonDetails(val person: People) : PersonMutation()
    data class SetFeedDisplay(val display: FeedDisplay) : PersonMutation()
}
