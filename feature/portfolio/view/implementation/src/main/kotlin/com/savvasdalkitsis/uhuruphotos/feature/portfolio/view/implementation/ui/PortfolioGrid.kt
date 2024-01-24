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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.seam.actions.PortfolioAction
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioItems
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.layout.plus

@Composable
fun PortfolioGrid(
    contentPadding: PaddingValues,
    localMedia: PortfolioItems.Found,
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
}