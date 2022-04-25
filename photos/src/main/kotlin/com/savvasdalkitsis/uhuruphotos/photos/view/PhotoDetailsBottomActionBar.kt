package com.savvasdalkitsis.uhuruphotos.photos.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.icons.R
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction.AskForPhotoDeletion
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction.SharePhoto
import com.savvasdalkitsis.uhuruphotos.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.ui.view.ActionIconWithText

@Composable
fun PhotoDetailsBottomActionBar(
    state: PhotoState,
    action: (PhotoAction) -> Unit,
) {
    Row {
        AnimatedVisibility(
            modifier = Modifier
                .weight(1f),
            visible = state.showShareIcon) {
            ActionIconWithText(
                onClick = { action(SharePhoto) },
                icon = R.drawable.ic_share,
                text = "Share",
            )
        }
        ActionIconWithText(
            onClick = { action(AskForPhotoDeletion) },
            modifier = Modifier
                .weight(1f),
            icon = R.drawable.ic_delete,
            text = "Delete",
        )
    }
}