package com.savvasdalkitsis.uhuruphotos.person.view.state

import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedState
import com.savvasdalkitsis.uhuruphotos.people.view.state.Person

data class PersonState(
    val person: Person? = null,
    val feedState: FeedState = FeedState(),
)


