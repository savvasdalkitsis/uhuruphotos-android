package com.savvasdalkitsis.librephotos.photos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.librephotos.photos.view.state.PhotoState
import com.savvasdalkitsis.librephotos.ui.insets.systemPadding
import com.savvasdalkitsis.librephotos.ui.view.zoom.ZoomableState

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
                .heightIn(min = max(100.dp, sheetSize.size.height - systemPadding(WindowInsetsSides.Top).calculateTopPadding()))
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

@Composable
private fun ColumnScope.SheetHandle() {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .background(MaterialTheme.colors.onBackground)
            .clip(RoundedCornerShape(4.dp))
            .align(Alignment.CenterHorizontally)
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.onBackground)
                .width(24.dp)
                .height(4.dp)
        )
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
