package com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.ui

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.GalleryPage
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.GalleryPageState
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosAction
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosAction.FingerPrintActionPressed
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either.Left
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either.Right
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.view.ActionIcon

@Composable
fun HiddenPhotosAlbumPage(
    state: Pair<GalleryPageState, HiddenPhotosState>,
    action: (Either<GalleryPageAction, HiddenPhotosAction>) -> Unit
) {
    GalleryPage(
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