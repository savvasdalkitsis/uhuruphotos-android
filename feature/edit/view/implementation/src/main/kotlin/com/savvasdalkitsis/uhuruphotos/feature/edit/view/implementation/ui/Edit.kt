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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.actions.EditAction
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.actions.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.ui.state.EditState
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.blurIf
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UpNavButton
import io.moyuru.cropify.rememberCropifyState

@Composable
internal fun Edit(
    state: EditState,
    action: (EditAction) -> Unit,
) {
    val cropState = rememberCropifyState()
    CommonScaffold(
        modifier = Modifier.blurIf(state.isLoading),
        title = { Text(stringResource(string.crop)) },
        actionBarContent = {
            EditActionBar(state, action, cropState)
        },
        navigationIcon = {
            UpNavButton {
                action(NavigateBack)
            }
        }
    ) {
        EditCanvas(state, cropState, action)
    }
    if (state.isLoading) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background.copy(alpha = 0.1f))
            .pointerInput(Unit) {
            }
        ) {
            FullLoading()
        }
    }
}

@Preview
@Composable
fun CropOptionsPreview() {
    PreviewAppTheme {
        Box {
            EditCropOptions {}
        }
    }
}