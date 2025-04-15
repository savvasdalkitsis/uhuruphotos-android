/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.seam.actions.PortfolioAction
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioCelState
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioItemsState.FoundState
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioItemsState.LoadingState
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioItemsState.RequiresPermissionsState
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioState
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UhuruFullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.SelectionMode.SELECTED
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.SelectionMode.UNSELECTED
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruUpNavButton
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun Portfolio(
    state: PortfolioState,
    action: (PortfolioAction) -> Unit = {},
) {
    UhuruScaffold(
        title = { Text(text = state.title ?: "") },
        navigationIcon = { UhuruUpNavButton() },
    ) { contentPadding ->
        when(state.localMedia) {
            is FoundState -> PortfolioGrid(contentPadding, state.localMedia, state.showScanOther, action)
            LoadingState -> UhuruFullLoading()
            is RequiresPermissionsState -> PortfolioMissingPermissions(state.localMedia.deniedPermissions)
        }
    }
}

@Preview
@Composable
private fun PortfolioPreview() {
    PreviewAppTheme {
        Portfolio(state = PortfolioState(
            FoundState(
                List(50) { state(it) }.toImmutableList()
            )
        ))
    }
}

@Preview
@Composable
private fun PortfolioNotOthersPreview() {
    PreviewAppTheme {
        Portfolio(state = PortfolioState(
            localMedia = FoundState(persistentListOf(state(1))),
            showScanOther = true,
        ))
    }
}

@Preview
@Composable
private fun PortfolioLoadingPreview() {
    PreviewAppTheme {
        Portfolio(state = PortfolioState(LoadingState))
    }
}

@Preview
@Composable
private fun PortfolioNoPermissionsPreview() {
    PreviewAppTheme {
        Portfolio(state = PortfolioState(RequiresPermissionsState(persistentListOf())))
    }
}

@Composable
internal fun state(index: Int) = PortfolioCelState(
    selection = if (index % 2 == 0) SELECTED else UNSELECTED,
    clickable = index > 0,
    folder = LocalMediaFolder(index, "Folder $index"),
    vitrine = VitrineState()
)