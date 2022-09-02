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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.Collage
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.SearchAction
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.SearchAction.SelectedCel
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state.SearchResults
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state.SearchState
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.copy

@Composable
fun SearchFeed(
    state: SearchState,
    contentPadding: PaddingValues,
    searchResults: SearchResults.Found,
    action: (SearchAction) -> Unit
) {
    Collage(
        contentPadding = contentPadding.copy(top = 0.dp),
        onCelSelected = { cel, center, scale ->
            action(SelectedCel(cel, center, scale))
        },
        onChangeDisplay = { action(SearchAction.ChangeDisplay(it)) },
        state = CollageState(
            isLoading = false,
            clusters = searchResults.clusters,
            collageDisplay = state.searchDisplay,
        ),
    )
}