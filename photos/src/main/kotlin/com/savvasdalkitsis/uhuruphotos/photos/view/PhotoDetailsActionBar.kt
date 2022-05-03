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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.icons.R
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.uhuruphotos.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.ui.view.ActionIcon

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