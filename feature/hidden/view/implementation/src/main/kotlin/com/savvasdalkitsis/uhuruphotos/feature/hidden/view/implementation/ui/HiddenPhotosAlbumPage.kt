package com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.ui

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.Gallery
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.actions.HiddenPhotosAction
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.actions.FingerPrintActionPressed
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either.Left
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either.Right
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon

@Composable
fun HiddenPhotosAlbumPage(
    state: Pair<GalleryState, HiddenPhotosState>,
    action: (Either<GalleryAction, HiddenPhotosAction>) -> Unit
) {
    Gallery(
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
            action(Left(it))
        }
    )
}