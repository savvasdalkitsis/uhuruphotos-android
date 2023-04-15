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
package com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions.SearchAction
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state.SearchResults.Found
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state.SearchResults.Idle
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state.SearchResults.Searching
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state.SearchState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullProgressBar

@Composable fun Search(
    state: SearchState,
    action: (SearchAction) -> Unit,
    contentPadding: PaddingValues,
) {
    Column {
        Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
        SearchField(state, action)
        when (state.searchResults) {
            Idle -> SearchIdle(state, action)
            Searching -> FullProgressBar()
            is Found -> SearchFeed(state, contentPadding, state.searchResults, action)
        }
    }
}