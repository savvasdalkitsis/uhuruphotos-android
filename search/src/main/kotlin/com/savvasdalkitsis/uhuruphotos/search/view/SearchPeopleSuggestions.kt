package com.savvasdalkitsis.uhuruphotos.search.view

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.people.api.view.PeopleBar
import com.savvasdalkitsis.uhuruphotos.people.api.view.state.Person
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction.PersonSelected
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction.ViewAllPeopleSelected

@Composable
fun SearchPeopleSuggestions(
    people: List<Person>,
    action: (SearchAction) -> Unit,
) {
    PeopleBar(
        people = people,
        onViewAllClicked = { action(ViewAllPeopleSelected) },
        onPersonSelected = { action(PersonSelected(it)) }
    )
}