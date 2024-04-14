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

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.Collage
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.CollageDisplayActionButton
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions.SearchAction
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions.SearchFor
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions.SelectedCel
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state.SearchState
import com.savvasdalkitsis.uhuruphotos.foundation.search.view.implementation.R
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.NoContent
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UpNavButton

@Composable
fun Search(
    state: SearchState,
    action: (SearchAction) -> Unit
) {
    CommonScaffold(
        title = { Text(state.query.ifEmpty { stringResource(string.searching) }) },
        actionBarContent = {
            CollageDisplayActionButton(
                onChange = { action(ChangeDisplay(it)) },
                currentCollageDisplay = state.searchDisplay
            )
        },
        navigationIcon = { UpNavButton() }
    ) { contentPadding ->
        if (state.isError) {
            NoContent(string.error_searching) {
                Button(onClick = { action(SearchFor(state.query)) }) {
                    Text(text = stringResource(string.retry))
                }
            }
        } else {
            Collage(
                contentPadding = contentPadding,
                state = CollageState(
                    isLoading = state.isLoading,
                    clusters = state.clusters,
                    collageDisplay = state.searchDisplay,
                ),
                loadingAnimation = R.raw.animation_searching,
                onCelSelected = { cel ->
                    action(SelectedCel(cel))
                },
                onChangeDisplay = { action(ChangeDisplay(it)) },
            )
        }
    }
}