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
package com.savvasdalkitsis.uhuruphotos.feature.upload.view.api.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.upload.view.api.ui.state.UploadErrorDialogMode
import com.savvasdalkitsis.uhuruphotos.feature.upload.view.api.ui.state.UploadErrorDialogMode.ERROR_CHECKING
import com.savvasdalkitsis.uhuruphotos.feature.upload.view.api.ui.state.UploadErrorDialogMode.NOT_ALLOWED
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.dialogs.OkDialog
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun UploadErrorDialog(
    mode: UploadErrorDialogMode,
    onDismiss: () -> Unit = {},
) {
    OkDialog(
        title = stringResource(strings.cannot_upload),
        onDismiss = onDismiss,
    ) {
        Text(
            stringResource(when (mode) {
                NOT_ALLOWED -> strings.upload_not_allowed
                ERROR_CHECKING -> strings.upload_error_checking
            })
        )
    }
}