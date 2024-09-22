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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.actions.EditAction
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.actions.SelectCropRatio
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.ui.state.CropRatioState
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.ActionIconWithText

@Composable
internal fun EditCropOptions(
    action: (EditAction) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        for (ratio in CropRatioState.entries) {
            ActionIconWithText(
                onClick = { action(SelectCropRatio(ratio)) },
                icon = ratio.icon,
                iconModifier = Modifier.rotate(ratio.rotation),
                text = ratio.label,
            )
        }
    }
}

@Preview
@Composable
private fun EditCropOptionsPreview() {
    PreviewAppTheme {
        EditCropOptions {}
    }
}