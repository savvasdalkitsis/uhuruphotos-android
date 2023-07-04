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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.CollageDisplayActionButton
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.AskForSelectedPhotosTrashing
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.DownloadSelectedCels
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.FeedAction
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.ShareSelectedCels
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon

@Composable
internal fun RowScope.FeedActionBar(
    shouldShowShareIcon: Boolean,
    shouldShowDeleteIcon: Boolean,
    shouldShowDownloadIcon: Boolean,
    hasSelection: Boolean,
    collageDisplay: CollageDisplay,
    action: (FeedAction) -> Unit
) {
    AnimatedVisibility(visible = shouldShowShareIcon) {
        ActionIcon(
            onClick = { action(ShareSelectedCels) },
            icon = drawable.ic_share
        )
    }
    AnimatedVisibility(visible = shouldShowDeleteIcon) {
        ActionIcon(
            onClick = { action(AskForSelectedPhotosTrashing) },
            icon = drawable.ic_delete
        )
    }
    AnimatedVisibility(visible = shouldShowDownloadIcon) {
        ActionIcon(
            onClick = { action(DownloadSelectedCels) },
            icon = drawable.ic_cloud_download
        )
    }
    AnimatedVisibility(visible = !hasSelection) {
        CollageDisplayActionButton(
            onChange = { action(ChangeDisplay(it as PredefinedCollageDisplay)) },
            currentCollageDisplay = collageDisplay
        )
    }
}