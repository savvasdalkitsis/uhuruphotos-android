/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.photos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.uhuruphotos.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.ui.insets.insetsTop
import com.savvasdalkitsis.uhuruphotos.ui.view.SheetHandle
import com.savvasdalkitsis.uhuruphotos.ui.view.SheetSize
import com.savvasdalkitsis.uhuruphotos.ui.view.zoom.ZoomableState

@Composable
internal fun BottomSheetPhotoDetails(
    state: PhotoState,
    zoomableState: ZoomableState,
    action: (PhotoAction) -> Unit
) {
    val infoSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val sheetSize by SheetSize.rememberSheetSize()

    ModalBottomSheetLayout(
        sheetShape = RoundedCornerShape(12.dp),
        sheetContent = {
            Column(modifier = Modifier
                .heightIn(min = max(100.dp, sheetSize.size.height - insetsTop()))
                .background(MaterialTheme.colors.background)
            ) {
                SheetHandle()
                PhotoDetailsSheet(
                    sheetSize = sheetSize,
                    state = state,
                    sheetState = infoSheetState.toSheetState(),
                    zoomableState = zoomableState,
                    action = action
                )
            }
        },
        sheetState = infoSheetState
    ) {
        PhotoDetailsScaffold(sheetSize, state, action, zoomableState)
    }
}

private fun ModalBottomSheetState.toSheetState() = object : SheetState {
    override val isHidden: Boolean
        get() = currentValue == ModalBottomSheetValue.Hidden

    override suspend fun hide() {
        this@toSheetState.hide()
    }

    override suspend fun show() {
        this@toSheetState.show()
    }
}
