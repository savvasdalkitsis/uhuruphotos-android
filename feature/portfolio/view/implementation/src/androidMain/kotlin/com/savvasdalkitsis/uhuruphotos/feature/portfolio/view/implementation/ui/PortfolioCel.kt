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

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.NamedVitrine
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.seam.actions.ChangePortfolioItem
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.seam.actions.NavigateToFolder
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.seam.actions.PortfolioAction
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioCelState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.CustomColors
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.Checkable
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.SelectionMode.SELECTED
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.SelectionMode.UNDEFINED
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.SelectionMode.UNSELECTED

@Composable
fun PortfolioCel(
    cel: PortfolioCelState,
    action: (PortfolioAction) -> Unit
) {
    Checkable(
        modifier = Modifier.fillMaxHeight(1f),
        id = cel.folder.id,
        shape = MaterialTheme.shapes.small,
        selectionMode = cel.selection,
        selectionBackgroundColor = CustomColors.selectedBackground,
        editable = cel.clickable,
        onClick = {
            when(cel.selection) {
                UNDEFINED -> action(NavigateToFolder(cel.folder))
                SELECTED -> action(ChangePortfolioItem(cel.folder, false))
                UNSELECTED -> action(ChangePortfolioItem(cel.folder, true))
            }
        }
    ) {
        NamedVitrine(
            modifier = Modifier.fillMaxWidth(),
            state = cel.vitrine,
            photoGridModifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            iconFallback = images.ic_folder,
            title = cel.folder.displayName,
            selectable = false,
        ) {
        }
    }
}