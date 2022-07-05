package com.savvasdalkitsis.uhuruphotos.implementation.hidden.view

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.AlbumPage
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumPageState
import com.savvasdalkitsis.uhuruphotos.api.icons.R
import com.savvasdalkitsis.uhuruphotos.api.seam.Either
import com.savvasdalkitsis.uhuruphotos.api.seam.Either.*
import com.savvasdalkitsis.uhuruphotos.api.ui.view.ActionIcon
import com.savvasdalkitsis.uhuruphotos.implementation.hidden.seam.HiddenPhotosAction
import com.savvasdalkitsis.uhuruphotos.implementation.hidden.seam.HiddenPhotosAction.*
import com.savvasdalkitsis.uhuruphotos.implementation.hidden.seam.HiddenPhotosState

@Composable
fun HiddenPhotosAlbumPage(
    state: Pair<AlbumPageState, HiddenPhotosState>,
    action: (Either<AlbumPageAction, HiddenPhotosAction>) -> Unit
) {
    AlbumPage(
        state = state.first,
        additionalActionBarContent = {
            if (state.second.displayFingerPrintAction) {
                ActionIcon(
                    onClick = {
                        action(Right(FingerPrintActionPressed))
                    },
                    icon = R.drawable.ic_fingerprint,
                )
            }
        },
        action = {
            action(Left(it))
        }
    )
}