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
@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.seam.actions.PortfolioAction
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.seam.actions.StartScanningOtherFolders
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioItemsState
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.layout.plus
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.button.UhuruIconOutlineButton
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_folder

@Composable
fun BoxScope.PortfolioGrid(
    contentPadding: PaddingValues = PaddingValues(),
    localMedia: PortfolioItemsState.FoundState,
    showScanOther: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    action: (PortfolioAction) -> Unit
) {
    val padding = 4.dp
    with (sharedTransitionScope) {
        LazyVerticalGrid(
            contentPadding = contentPadding + PaddingValues(horizontal = padding),
            columns = GridCells.Adaptive(minSize = 120.dp),
            horizontalArrangement = spacedBy(padding),
            verticalArrangement = spacedBy(padding),
        ) {
            localMedia.buckets.forEachIndexed { index, cel ->
                item(index) {
                    PortfolioCel(cel, action)
                }
            }
        }
    }
    if (showScanOther) {
        UhuruIconOutlineButton(
            modifier = Modifier.align(Alignment.Center),
            icon = drawable.ic_folder,
            text = "Scan other device folders",
        ) {
            action(StartScanningOtherFolders)
        }
    }
}
@Preview
@Composable
private fun PortfolioGridPreview() {
    PreviewAppTheme {
        val scope = this
        Box {
            PortfolioGrid(
                localMedia = PortfolioItemsState.FoundState(
                    List(50) { state(it) }.toImmutableList()
                ),
                showScanOther = false,
                sharedTransitionScope = scope,
            ) {}
        }
    }
}

@Preview
@Composable
private fun PortfolioGridNotOthersPreview() {
    PreviewAppTheme {
        val scope = this
        Box {
            PortfolioGrid(
                localMedia = PortfolioItemsState.FoundState(
                    persistentListOf(state(1))
                ),
                showScanOther = true,
                sharedTransitionScope = scope,
            ) {}
        }
    }
}
