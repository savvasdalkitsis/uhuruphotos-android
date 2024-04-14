/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui

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
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.DiscoverAction
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.QueryCacheChanged
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.QueryChanged
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.SearchFor

@Composable
fun SearchSuggestion(
    suggestion: String,
    action: (DiscoverAction) -> Unit
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
            action(QueryChanged(suggestion))
            action(QueryCacheChanged)
            action(SearchFor(suggestion))
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