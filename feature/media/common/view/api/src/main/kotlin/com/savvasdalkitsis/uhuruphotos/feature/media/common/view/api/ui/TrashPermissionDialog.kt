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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.dialogs.YesNoDialog
import org.jetbrains.compose.resources.pluralStringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.plurals
import uhuruphotos_android.foundation.strings.api.generated.resources.trash_media
import uhuruphotos_android.foundation.strings.api.generated.resources.trash_media_confirmation

@Composable
fun TrashPermissionDialog(
    mediaItemCount: Int,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    YesNoDialog(
        title = pluralStringResource(
            plurals.trash_media,
            mediaItemCount,
            mediaItemCount
        ),
        onDismiss = onDismiss,
        onYes = onDelete,
    ) {
        Text(
            pluralStringResource(
                plurals.trash_media_confirmation,
                quantity = mediaItemCount,
                mediaItemCount
            )
        )
    }
}