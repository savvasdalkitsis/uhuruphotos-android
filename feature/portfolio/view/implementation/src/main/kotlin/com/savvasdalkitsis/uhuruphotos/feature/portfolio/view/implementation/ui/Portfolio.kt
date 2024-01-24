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

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.seam.actions.PortfolioAction
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioCelState
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioItems.Found
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioItems.Loading
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioItems.RequiresPermissions
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioState
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UpNavButton
import kotlinx.collections.immutable.toPersistentList

@Composable
internal fun Portfolio(
    state: PortfolioState,
    action: (PortfolioAction) -> Unit = {},
) {
    CommonScaffold(
        title = { Text(text = stringResource(string.folders_on_feed)) },
        navigationIcon = { UpNavButton() },
    ) { contentPadding ->
        when(state.localMedia) {
            is Found -> PortfolioGrid(contentPadding, state.localMedia, action)
            Loading -> FullLoading()
            is RequiresPermissions -> PortfolioMissingPermissions(state.localMedia.deniedPermissions)
        }
    }
}

@Preview
@Composable
fun PortfolioPreview() {
    PreviewAppTheme {
        Portfolio(state = PortfolioState(
            Found(
                List(50) { state(it) }.toPersistentList()
            )
        ))
    }
}

@Preview
@Composable
fun PortfolioLoadingPreview() {
    PreviewAppTheme {
        Portfolio(state = PortfolioState(Loading))
    }
}

@Preview
@Composable
fun PortfolioNoPermissionsPreview() {
    PreviewAppTheme {
        Portfolio(state = PortfolioState(RequiresPermissions(emptyList())))
    }
}

@Composable
private fun state(index: Int) = PortfolioCelState(
    selected = index % 2 == 0,
    editable = index > 0,
    folder = LocalMediaFolder(index, "Folder $index"),
    vitrine = VitrineState()
)