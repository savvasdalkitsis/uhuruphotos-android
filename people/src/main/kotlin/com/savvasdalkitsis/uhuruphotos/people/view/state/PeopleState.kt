package com.savvasdalkitsis.uhuruphotos.people.view.state

data class PeopleState(
    val people: List<Person> = emptyList(),
    val sortOrder: SortOrder = SortOrder.default,
)


