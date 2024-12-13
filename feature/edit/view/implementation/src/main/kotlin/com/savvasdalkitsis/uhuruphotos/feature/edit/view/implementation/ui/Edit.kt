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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.actions.EditAction
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.ui.state.EditState
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.blurIf
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UhuruFullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruUpNavButton
import io.moyuru.cropify.rememberCropifyState

@Composable
internal fun Edit(
    state: EditState,
    action: (EditAction) -> Unit,
) {
    val cropState = rememberCropifyState()
    UhuruScaffold(
        modifier = Modifier.blurIf(state.isLoading),
        title = { Text(stringResource(string.crop)) },
        actionBarContent = {
            EditActionBar(state, action, cropState)
        },
        navigationIcon = { UhuruUpNavButton() }
    ) {
        EditCanvas(state, cropState, action)
    }
    if (state.isLoading) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.1f))
            .pointerInput(Unit) {
            }
        ) {
            UhuruFullLoading()
        }
    }
}

@Preview
@Composable
private fun CropOptionsPreview() {
    PreviewAppTheme {
        Box {
            EditCropOptions {}
        }
    }
}