package com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.seam.action.DeleteAutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.seam.action.DeleteAutoAlbumCancelled
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.seam.action.DeleteAutoAlbumRequested
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.viewmodel.AutoAlbumCompositeAction
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.viewmodel.AutoAlbumCompositeState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.Gallery
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.dialogs.YesNoDialog
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.AlertText

@Composable
internal fun AutoAlbum(
    state: AutoAlbumCompositeState,
    action: (AutoAlbumCompositeAction) -> Unit
) {
    Gallery(
        state = state.first,
        additionalActionBarContent = {
            ActionIcon(
                onClick = { action(Either.Right(DeleteAutoAlbumRequested)) },
                icon = R.drawable.ic_delete,
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
                style = MaterialTheme.typography.h6,
            )
            Text(text = stringResource(string.delete_album_explanation))
        }
    }
}