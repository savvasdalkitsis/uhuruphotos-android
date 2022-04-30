package com.savvasdalkitsis.uhuruphotos.people.viewmodel

import com.savvasdalkitsis.uhuruphotos.db.people.People
import com.savvasdalkitsis.uhuruphotos.people.view.state.SortOrder

sealed class PeopleMutation {
    data class DisplayPeople(val people: List<People>) : PeopleMutation()
    data class SetSortOrder(val sortOrder: SortOrder) : PeopleMutation()
}
