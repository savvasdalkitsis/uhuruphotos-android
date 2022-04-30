package com.savvasdalkitsis.uhuruphotos.people.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.icons.R
import com.savvasdalkitsis.uhuruphotos.people.view.state.PeopleState
import com.savvasdalkitsis.uhuruphotos.people.view.state.SortOrder.ASCENDING
import com.savvasdalkitsis.uhuruphotos.people.view.state.SortOrder.DESCENDING
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleAction
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleAction.*
import com.savvasdalkitsis.uhuruphotos.ui.view.*
import com.savvasdalkitsis.uhuruphotos.ui.window.WindowSize
import com.savvasdalkitsis.uhuruphotos.ui.window.WindowSizeClass.*

@Composable
fun People(
    state: PeopleState,
    action: (PeopleAction) -> Unit,
) {
    CommonScaffold(
        title = { Text("People") },
        navigationIcon = { BackNavButton {
            action(NavigateBack)
        } },
        actionBarContent = {
            ActionIcon(onClick = { action(ToggleSortOrder) }, icon = when (state.sortOrder) {
                ASCENDING -> R.drawable.ic_sort_az_ascending
                DESCENDING -> R.drawable.ic_sort_az_descending
            })
        }
    ) { contentPadding ->
        if (state.people.isEmpty()) {
            FullProgressBar()
        } else {
            val columns = when (WindowSize.LOCAL_WIDTH.current) {
                COMPACT -> 4
                MEDIUM -> 6
                EXPANDED -> 9
            }
            LazyVerticalGrid(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                contentPadding = contentPadding,
                columns = GridCells.Fixed(columns),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (person in state.people) {
                    item {
                        PersonThumbnail(
                            person = person,
                            shape = RoundedCornerShape(12.dp),
                            onPersonSelected = { action(PersonSelected(person)) }
                        )
                    }
                }
            }
        }
    }
}