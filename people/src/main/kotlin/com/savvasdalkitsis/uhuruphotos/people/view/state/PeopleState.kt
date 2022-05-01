package com.savvasdalkitsis.uhuruphotos.people.view.state

import com.savvasdalkitsis.uhuruphotos.people.api.view.state.Person

data class PeopleState(
    val people: List<Person> = emptyList(),
    val sortOrder: SortOrder = SortOrder.default,
)


