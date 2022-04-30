package com.savvasdalkitsis.uhuruphotos.search.view

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.people.view.PersonThumbnail
import com.savvasdalkitsis.uhuruphotos.people.view.state.Person
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction.PersonSelected
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction.ViewAllPeopleSelected

@Composable
fun SearchPeopleSuggestions(
    people: List<Person>,
    action: (SearchAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "People"
            )
            TextButton(onClick = { action(ViewAllPeopleSelected) }) {
                Text("View all")
            }
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Spacer(modifier = Modifier)
            for (person in people) {
                PersonThumbnail(
                    modifier = Modifier.width(90.dp),
                    person = person,
                    onPersonSelected = { action(PersonSelected(person)) },
                )
            }
            Spacer(modifier = Modifier)
        }
    }
}