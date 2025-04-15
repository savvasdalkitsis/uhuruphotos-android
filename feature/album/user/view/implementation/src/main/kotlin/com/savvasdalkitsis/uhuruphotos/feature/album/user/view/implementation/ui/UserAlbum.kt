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
package com.savvasdalkitsis.uhuruphotos.feature.album.user.view.implementation.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.savvasdalkitsis.uhuruphotos.feature.album.user.view.implementation.seam.action.DeleteUserAlbum
import com.savvasdalkitsis.uhuruphotos.feature.album.user.view.implementation.seam.action.DeleteUserAlbumCancelled
import com.savvasdalkitsis.uhuruphotos.feature.album.user.view.implementation.seam.action.DeleteUserAlbumRequested
import com.savvasdalkitsis.uhuruphotos.feature.album.user.view.implementation.ui.state.UserAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.album.user.view.implementation.viewmodel.UserAlbumCompositeAction
import com.savvasdalkitsis.uhuruphotos.feature.album.user.view.implementation.viewmodel.UserAlbumCompositeState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.Gallery
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.dialogs.YesNoDialog
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.AlertText
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.delete_album
import uhuruphotos_android.foundation.strings.api.generated.resources.delete_album_confirmation
import uhuruphotos_android.foundation.strings.api.generated.resources.delete_album_explanation

@Composable
internal fun UserAlbum(
    state: UserAlbumCompositeState,
    action: (UserAlbumCompositeAction) -> Unit
) {
    Gallery(
        state = state.first,
        additionalActionBarContent = {
            UhuruActionIcon(
                onClick = { action(Either.Right(DeleteUserAlbumRequested)) },
                icon = drawable.ic_delete,
            )
        },
        action = { action(Either.Left(it)) },
    )
    if (state.second.showDeleteConfirmationDialog) {
        YesNoDialog(
            title = stringResource(string.delete_album),
            onDismiss = { action(Either.Right(DeleteUserAlbumCancelled)) },
            onYes = { action(Either.Right(DeleteUserAlbum)) },
        ) {
            AlertText(
                text = stringResource(string.delete_album_confirmation),
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(text = stringResource(string.delete_album_explanation))
        }
    }
}

@Preview
@Composable
fun UserAlbumPreview() {
    PreviewAppTheme {
        UserAlbum(
            state = GalleryState() to UserAlbumState(showDeleteConfirmationDialog = true),
            action = {},
        )
    }
}