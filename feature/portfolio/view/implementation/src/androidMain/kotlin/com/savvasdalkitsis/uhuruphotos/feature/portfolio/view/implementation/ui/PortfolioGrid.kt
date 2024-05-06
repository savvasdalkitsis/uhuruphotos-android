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

import androidx.compose.foundation.layout.Arrangement.spacedBy
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
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioItems
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.layout.plus
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.button.IconOutlineButton
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

@Composable
fun BoxScope.PortfolioGrid(
    contentPadding: PaddingValues = PaddingValues(),
    localMedia: PortfolioItems.Found,
    showScanOther: Boolean,
    action: (PortfolioAction) -> Unit
) {
    val padding = 4.dp
    LazyVerticalGrid(
        contentPadding = contentPadding + PaddingValues(horizontal = padding),
        columns = GridCells.Adaptive(minSize = 120.dp),
        horizontalArrangement = spacedBy(padding),
        verticalArrangement = spacedBy(padding),
    ) {
        localMedia.buckets.forEach { cel ->
            item(cel.folder.id) {
                PortfolioCel(cel, action)
            }
        }
    }
    if (showScanOther) {
        IconOutlineButton(
            modifier = Modifier.align(Alignment.Center),
            icon = images.ic_folder,
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
        PortfolioGrid(
            localMedia = PortfolioItems.Found(
                List(50) { state(it) }.toPersistentList()
            ),
            showScanOther = false,
        ) {}
    }
}

@Preview
@Composable
private fun PortfolioGridNotOthersPreview() {
    PreviewAppTheme {
        PortfolioGrid(
            localMedia = PortfolioItems.Found(
                persistentListOf(state(1))
            ),
            showScanOther = true,
        ) {}
    }
}
