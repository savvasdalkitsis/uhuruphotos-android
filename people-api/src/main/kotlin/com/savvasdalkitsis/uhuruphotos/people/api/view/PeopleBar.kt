package com.savvasdalkitsis.uhuruphotos.people.api.view

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.people.api.view.state.Person

@Composable
fun PeopleBar(
    people: List<Person>,
    onViewAllClicked: (() -> Unit)? = null,
    onPersonSelected: (Person) -> Unit,
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
            if (onViewAllClicked != null) {
                TextButton(onClick = onViewAllClicked) {
                    Text("View all")
                }
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
                    onPersonSelected = { onPersonSelected(person) },
                )
            }
            Spacer(modifier = Modifier)
        }
    }
}