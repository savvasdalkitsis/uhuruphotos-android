package com.savvasdalkitsis.uhuruphotos.people.viewmodel

import com.savvasdalkitsis.uhuruphotos.people.view.state.Person

sealed class PeopleEffect {
    data class NavigateToPerson(val person: Person) : PeopleEffect()
    object ErrorLoadingPeople : PeopleEffect()
    object NavigateBack : PeopleEffect()
}
