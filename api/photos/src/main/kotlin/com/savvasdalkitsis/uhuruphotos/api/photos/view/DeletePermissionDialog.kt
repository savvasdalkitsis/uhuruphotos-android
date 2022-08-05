/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.api.photos.view

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.api.strings.R.string
import com.savvasdalkitsis.uhuruphotos.api.ui.view.YesNoDialog

@Composable
fun DeletePermissionDialog(
    photoCount: Int,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    YesNoDialog(
        title = pluralStringResource(
            R.plurals.delete_photos,
            photoCount,
            photoCount
        ),
        onDismiss = onDismiss,
        onYes = onDelete,
        body = {
            Text(
                pluralStringResource(
                    R.plurals.delete_photos_confirmation,
                    count = photoCount,
                    photoCount
                )
            )
            Text(stringResource(string.operation_irreverisble))
        })
}