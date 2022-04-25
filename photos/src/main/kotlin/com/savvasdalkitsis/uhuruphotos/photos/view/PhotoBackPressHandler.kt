package com.savvasdalkitsis.uhuruphotos.photos.view

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.uhuruphotos.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.ui.view.BackPressHandler

@Composable
fun PhotoBackPressHandler(
    state: PhotoState,
    action: (PhotoAction) -> Unit
) {
    BackPressHandler {
        if (state.infoSheetHidden) {
            action(PhotoAction.NavigateBack)
        } else {
            action(PhotoAction.HideInfo)
        }
    }
}