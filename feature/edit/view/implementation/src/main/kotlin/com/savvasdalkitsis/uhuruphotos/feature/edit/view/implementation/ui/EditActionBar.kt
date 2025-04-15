/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.actions.EditAction
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.actions.ToggleCropOptions
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.ui.state.EditState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruActionIcon
import io.moyuru.cropify.CropifyState
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.crop
import uhuruphotos_android.foundation.strings.api.generated.resources.crop_options

@Composable
internal  fun RowScope.EditActionBar(
    state: EditState,
    action: (EditAction) -> Unit,
    cropState: CropifyState
) {
    AnimatedVisibility(visible = state.isLoading) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(26.dp)
            )
        }
    }
    UhuruActionIcon(
        onClick = { action(ToggleCropOptions) },
        enabled = state.actionBarActionsEnabled,
        icon = R.drawable.ic_crop_free,
        contentDescription = stringResource(string.crop_options),
    )
    UhuruActionIcon(
        onClick = { cropState.crop() },
        enabled = state.actionBarActionsEnabled,
        icon = R.drawable.ic_crop,
        contentDescription = stringResource(string.crop),
    )
}