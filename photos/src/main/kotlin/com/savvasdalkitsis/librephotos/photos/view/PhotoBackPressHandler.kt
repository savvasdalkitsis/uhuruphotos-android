package com.savvasdalkitsis.librephotos.photos.view

import androidx.compose.material.ModalBottomSheetValue
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
        if (state.infoSheetState != ModalBottomSheetValue.Hidden) {
            action(PhotoAction.HideInfo)
        } else {
            action(PhotoAction.NavigateBack)
        }
    }
}