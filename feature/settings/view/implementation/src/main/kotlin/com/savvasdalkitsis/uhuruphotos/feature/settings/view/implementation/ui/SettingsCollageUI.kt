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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.CollageShapeChanged
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.CollageSpacingChanged
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.CollageSpacingEdgeChanged
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.Checkable
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollageShape
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollageShape.RECTANGLE
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollageShape.ROUNDED_RECTANGLE
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.IconText
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.SelectionMode.SELECTED
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.SelectionMode.UNSELECTED

@Composable
internal fun SettingsCollageUI(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    SettingsSliderRow(
        text = {
            stringResource(string.collage_spacing, it.toInt())
        },
        initialValue = state.collageSpacing.toFloat(),
        range = 0f..5f,
        steps = 6,
        onValueChanged = { action(CollageSpacingChanged(it.toInt())) }
    )
    SettingsCheckBox(
        text = stringResource(string.collage_spacing_include_edges),
        icon = drawable.ic_border_outside,
        isChecked = state.collageSpacingIncludeEdges,
        onCheckedChange = { action(CollageSpacingEdgeChanged(it)) },
    )
    SettingsTextRow(stringResource(string.collage_shape))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ShapeButton(
            shape = RECTANGLE,
            icon = drawable.ic_rectangle,
            text = string.rectangle,
            state = state,
            action = action
        )
        ShapeButton(
            shape = ROUNDED_RECTANGLE,
            icon = drawable.ic_rounded_rectangle,
            text = string.rounded_rectangle,
            state = state,
            action = action
        )
    }
}

@Composable
private fun RowScope.ShapeButton(
    shape: CollageShape,
    @DrawableRes icon: Int,
    @StringRes text: Int,
    state: SettingsState,
    action: (SettingsAction) -> Unit
) {
    Checkable(
        modifier = Modifier.weight(1f),
        shape = MaterialTheme.shapes.small,
        id = "collageShape$shape",
        selectionMode = when {
            state.collageShape == shape -> SELECTED
            else -> UNSELECTED
        },
        onClick = { action(CollageShapeChanged(shape)) },
    ) {
        Box(modifier = Modifier
            .heightIn(min = 64.dp)
            .fillMaxWidth()
            .padding(16.dp),
        ) {
            IconText(
                modifier = Modifier
                    .align(Alignment.Center),
                icon = icon,
                text = stringResource(text),
            )
        }
    }
}