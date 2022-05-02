package com.savvasdalkitsis.uhuruphotos.search.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction

@Composable
fun SearchSuggestion(
    suggestion: String,
    action: (SearchAction) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(
                start = 12.dp,
                end = 12.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(modifier = Modifier.weight(1f), text = "Search suggestions:")
        OutlinedButton(onClick = {
            action(SearchAction.ChangeQuery(suggestion))
            action(SearchAction.SearchFor(suggestion))
        }) {
            Text(
                modifier = Modifier.widthIn(max =  160.dp),
                text = suggestion,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}