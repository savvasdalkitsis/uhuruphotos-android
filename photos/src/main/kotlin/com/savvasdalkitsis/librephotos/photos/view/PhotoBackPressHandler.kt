package com.savvasdalkitsis.librephotos.photos.view

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.librephotos.photos.view.state.PhotoState
import com.savvasdalkitsis.librephotos.ui.view.BackPressHandler

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