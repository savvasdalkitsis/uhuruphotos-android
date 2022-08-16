package com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.navigation

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomAction
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.ui.ShowroomPage
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.ui.state.ShowroomState
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashAction
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashAction.FingerPrintActionPressed
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.state.TrashState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either.Right
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon

@Composable
internal fun TrashAlbumPage(
    state: Pair<ShowroomState, TrashState>,
    action: (Either<ShowroomAction, TrashAction>) -> Unit
) {
    ShowroomPage(
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