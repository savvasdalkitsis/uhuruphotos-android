package com.savvasdalkitsis.uhuruphotos.implementation.trash.navigation

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.AlbumPage
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumPageState
import com.savvasdalkitsis.uhuruphotos.api.icons.R.drawable
import com.savvasdalkitsis.uhuruphotos.api.seam.Either
import com.savvasdalkitsis.uhuruphotos.api.seam.Either.Right
import com.savvasdalkitsis.uhuruphotos.api.ui.view.ActionIcon
import com.savvasdalkitsis.uhuruphotos.implementation.trash.seam.TrashAction
import com.savvasdalkitsis.uhuruphotos.implementation.trash.seam.TrashAction.FingerPrintActionPressed
import com.savvasdalkitsis.uhuruphotos.implementation.trash.view.state.TrashState

@Composable
internal fun TrashAlbumPage(
    state: Pair<AlbumPageState, TrashState>,
    action: (Either<AlbumPageAction, TrashAction>) -> Unit
) {
    AlbumPage(
        state = state.first,
        additionalActionBarContent = {
            if (state.second.displayFingerPrintAction) {
                ActionIcon(
                    onClick = {
                        action(Right(FingerPrintActionPressed))
                    },
                    icon = drawable.ic_fingerprint,
                )
            }
        },
        action = {
            action(Either.Left(it))
        },
    )
}