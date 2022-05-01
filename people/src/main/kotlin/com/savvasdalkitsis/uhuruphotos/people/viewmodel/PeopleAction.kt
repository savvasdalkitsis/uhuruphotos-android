package com.savvasdalkitsis.uhuruphotos.people.viewmodel

import com.savvasdalkitsis.uhuruphotos.people.api.view.state.Person

sealed class PeopleAction {
    data class PersonSelected(val person: Person) : PeopleAction()
    object LoadPeople : PeopleAction()
    object ToggleSortOrder : PeopleAction()
    object NavigateBack : PeopleAction()
}
