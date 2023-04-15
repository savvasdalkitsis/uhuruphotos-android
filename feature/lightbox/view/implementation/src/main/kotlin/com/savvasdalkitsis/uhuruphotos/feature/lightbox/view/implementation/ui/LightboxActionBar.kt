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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.LightboxAction
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.DownloadOriginal
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.SetFavourite
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.ShowInfo
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.OriginalFileIconState.ERROR
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.OriginalFileIconState.HIDDEN
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.OriginalFileIconState.IDLE
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.OriginalFileIconState.IN_PROGRESS
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon

@Composable
fun LightboxActionBar(
    state: LightboxState,
    index: Int,
    action: (LightboxAction) -> Unit,
) {
    val mediaItem = state.media[index]
    AnimatedVisibility(visible = state.isLoading) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(26.dp)
            )
        }
    }
    state.currentMediaItem.mediaItemSyncState?.let { syncState ->
        ActionIcon(
            modifier = Modifier.alpha(0.7f),
            onClick = { },
            enabled = false,
            icon = syncState.icon,
            contentDescription = stringResource(syncState.contentDescription)
        )
    }
    AnimatedContent(targetState = mediaItem.originalFileIconState) {
        when (it) {
            IDLE -> ActionIcon(
                onClick = { action(DownloadOriginal(mediaItem)) },
                icon = drawable.ic_cloud_download,
                contentDescription = stringResource(string.download_original_file)
            )
            IN_PROGRESS -> ActionIcon(
                enabled = false,
                onClick = { },
                icon = drawable.ic_cloud_in_progress,
                contentDescription = stringResource(string.downloading_original_file)
            )
            ERROR -> ActionIcon(
                onClick = { action(DownloadOriginal(mediaItem)) },
                icon = drawable.ic_cloud_alert,
                contentDescription = stringResource(string.download_original_file)
            )
            HIDDEN -> {}
        }
    }
    AnimatedVisibility(visible = mediaItem.showFavouriteIcon && mediaItem.isFavourite != null) {
        if (mediaItem.showFavouriteIcon && mediaItem.isFavourite != null) {
            ActionIcon(
                onClick = { action(SetFavourite(!mediaItem.isFavourite)) },
                icon = if (mediaItem.isFavourite) drawable.ic_favourite else drawable.ic_not_favourite,
                contentDescription = stringResource(
                    when {
                        mediaItem.isFavourite -> string.remove_favourite
                        else -> string.favourite
                    }
                )
            )
        }
    }
    AnimatedVisibility(visible = state.showInfoButton) {
        if (state.showInfoButton) {
            ActionIcon(
                onClick = { action(ShowInfo) },
                icon = drawable.ic_info,
                contentDescription = stringResource(string.info),
            )
        }
    }
}