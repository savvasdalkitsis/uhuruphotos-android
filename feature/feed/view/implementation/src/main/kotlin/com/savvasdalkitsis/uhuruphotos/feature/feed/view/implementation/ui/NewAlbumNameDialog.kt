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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.CreateAlbumAndAddSelectedMedia
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.FeedAction
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.HideNewAlbumNameDialog
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.dialogs.YesNoDialog
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.cancel
import uhuruphotos_android.foundation.strings.api.generated.resources.create
import uhuruphotos_android.foundation.strings.api.generated.resources.create_album
import uhuruphotos_android.foundation.strings.api.generated.resources.new_album_name

@Composable
internal fun NewAlbumNameDialog(action: (FeedAction) -> Unit) {
    var name by remember { mutableStateOf("") }
    YesNoDialog(
        title = stringResource(string.create_album),
        yes = stringResource(string.create),
        onYes = { action(CreateAlbumAndAddSelectedMedia(name)) },
        no = stringResource(string.cancel),
        onNo = { action(HideNewAlbumNameDialog) },
    ) {
        val focusRequester = remember { FocusRequester() }

        Text(text = stringResource(string.new_album_name))
        TextField(
            modifier = Modifier
                .focusRequester(focusRequester),
            value = name,
            onValueChange = { name = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrectEnabled = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { action(CreateAlbumAndAddSelectedMedia(name)) },
            ),
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Preview
@Composable
private fun NewAlbumNameDialogPreview() {
    PreviewAppTheme {
        NewAlbumNameDialog {

        }
    }
}