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
@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.seam.action.DeleteAutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.seam.action.DeleteAutoAlbumCancelled
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.seam.action.DeleteAutoAlbumRequested
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.viewmodel.AutoAlbumCompositeAction
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.viewmodel.AutoAlbumCompositeState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.Gallery
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.dialogs.YesNoDialog
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.AlertText
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_delete
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.delete_album
import uhuruphotos_android.foundation.strings.api.generated.resources.delete_album_confirmation
import uhuruphotos_android.foundation.strings.api.generated.resources.delete_album_explanation

@Composable
internal fun SharedTransitionScope.AutoAlbum(
    state: AutoAlbumCompositeState,
    action: (AutoAlbumCompositeAction) -> Unit
) {
    Gallery(
        state = state.first,
        additionalActionBarContent = {
            UhuruActionIcon(
                onClick = { action(Either.Right(DeleteAutoAlbumRequested)) },
                icon = drawable.ic_delete,
            )
        },
        action = { action(Either.Left(it)) },
    )
    if (state.second.showDeleteConfirmationDialog) {
        YesNoDialog(
            title = stringResource(string.delete_album),
            onDismiss = { action(Either.Right(DeleteAutoAlbumCancelled)) },
            onYes = { action(Either.Right(DeleteAutoAlbum)) },
        ) {
            AlertText(
                text = stringResource(string.delete_album_confirmation),
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(text = stringResource(string.delete_album_explanation))
        }
    }
}