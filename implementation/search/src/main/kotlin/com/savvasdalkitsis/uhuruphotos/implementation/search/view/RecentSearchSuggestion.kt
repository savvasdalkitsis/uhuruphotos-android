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
package com.savvasdalkitsis.uhuruphotos.implementation.search.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.icons.R
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchAction
import com.savvasdalkitsis.uhuruphotos.api.ui.view.ActionIcon

internal fun LazyListScope.recentSearchSuggestion(
    suggestion: String,
    action: (SearchAction) -> Unit,
    onClick: () -> Unit,
) {
    item("recent-$suggestion", contentType = "recentSearchSuggestion") {
        Suggestion(
            modifier = Modifier.animateItemPlacement(),
            text = suggestion,
            onClick = onClick,
            leadingContent = {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    painter = painterResource(id = R.drawable.ic_history),
                    contentDescription = null
                )
            },
            trailingContent = {
                ActionIcon(
                    onClick = { action(SearchAction.RemoveFromRecentSearches(suggestion)) },
                    icon = R.drawable.ic_clear
                )
            }
        )
    }
}