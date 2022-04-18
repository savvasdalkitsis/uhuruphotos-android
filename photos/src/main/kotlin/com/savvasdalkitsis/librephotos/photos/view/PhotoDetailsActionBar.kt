package com.savvasdalkitsis.librephotos.photos.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.librephotos.icons.R
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.librephotos.photos.view.state.PhotoState
import com.savvasdalkitsis.librephotos.ui.view.ActionIcon

@Composable
fun PhotoDetailsActionBar(
    state: PhotoState,
    action: (PhotoAction) -> Unit,
) {
    AnimatedVisibility(visible = state.isLoading) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(26.dp)
            )
        }
    }
    AnimatedVisibility(visible = state.showRefresh) {
        if (state.showRefresh) {
            ActionIcon(
                onClick = { action(PhotoAction.Refresh) },
                icon = R.drawable.ic_refresh,
                contentDescription = "refresh"
            )
        }
    }
    AnimatedVisibility(visible = state.isFavourite != null) {
        if (state.isFavourite != null) {
            ActionIcon(
                onClick = { action(PhotoAction.SetFavourite(!state.isFavourite)) },
                icon = if (state.isFavourite) R.drawable.ic_favourite else R.drawable.ic_not_favourite,
                contentDescription = if (state.isFavourite) "favourite" else "not favourite"
            )
        }
    }
    AnimatedVisibility(visible = state.showInfoButton) {
        if (state.showInfoButton) {
            ActionIcon(
                onClick = { action(PhotoAction.ShowInfo) },
                icon = R.drawable.ic_info,
                contentDescription = "info",
            )
        }
    }
}