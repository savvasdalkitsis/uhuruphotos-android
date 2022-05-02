package com.savvasdalkitsis.uhuruphotos.photos.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.LayoutDirection.Rtl
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.uhuruphotos.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.ui.layout.WithDirection
import com.savvasdalkitsis.uhuruphotos.ui.layout.reverse
import com.savvasdalkitsis.uhuruphotos.ui.view.SheetSize
import com.savvasdalkitsis.uhuruphotos.ui.view.zoom.ZoomableState
import kotlin.math.min

@Composable
internal fun SideSheetPhotoDetails(
    state: PhotoState,
    zoomableState: ZoomableState,
    action: (PhotoAction) -> Unit
) {
    val sheetSize by SheetSize.rememberSheetSize()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val direction = LocalLayoutDirection.current

    val sheetMaxWidth = 460.dp
    WithDirection(direction.reverse) {
        ModalDrawer(
            gesturesEnabled = drawerState.isOpen,
            drawerState = drawerState,
            drawerShape = rectWithMaxWidth(sheetMaxWidth),
            drawerContent = {
                Box(modifier = Modifier.widthIn(max = sheetMaxWidth)) {
                    WithDirection(direction) {
                        PhotoDetailsSheet(
                            modifier = Modifier.fillMaxHeight(),
                            sheetSize = sheetSize,
                            state = state,
                            sheetState = drawerState.toSheetState(),
                            zoomableState = zoomableState,
                            action = action
                        )
                    }
                }
            },
        ) {
            WithDirection(direction) {
                PhotoDetailsScaffold(sheetSize, state, action, zoomableState)
            }
        }
    }
}

private fun rectWithMaxWidth(
    maxWidth: Dp,
) = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline.Rectangle {
        val width = min(size.width, with(density) { maxWidth.toPx() })
        val start = when(layoutDirection) {
            Ltr -> 0f
            Rtl -> size.width - width
        }
        val end = when(layoutDirection) {
            Ltr -> width
            Rtl -> size.width
        }
        return Outline.Rectangle(Rect(start, 0f, end, size.height))
    }
}

private fun DrawerState.toSheetState() = object : SheetState {
    override val isHidden: Boolean
        get() = isClosed

    override suspend fun hide() {
        close()
    }

    override suspend fun show() {
        open()
    }
}
