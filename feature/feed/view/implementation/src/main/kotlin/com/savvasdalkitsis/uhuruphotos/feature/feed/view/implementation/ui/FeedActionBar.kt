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
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.AskForSelectedPhotosTrashing
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.DownloadSelectedPhotos
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.ShareSelectedPhotos
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.GalleryDisplayActionButton
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.PredefinedGalleryDisplay
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon

@Composable
internal fun RowScope.FeedActionBar(
    state: FeedState,
    action: (FeedAction) -> Unit
) {
    AnimatedVisibility(visible = state.shouldShowShareIcon) {
        ActionIcon(
            onClick = { action(ShareSelectedPhotos) },
            icon = drawable.ic_share
        )
    }
    AnimatedVisibility(visible = state.hasSelection) {
        ActionIcon(
            onClick = { action(AskForSelectedPhotosTrashing) },
            icon = drawable.ic_delete
        )
    }
    AnimatedVisibility(visible = state.hasSelection) {
        ActionIcon(
            onClick = { action(DownloadSelectedPhotos) },
            icon = drawable.ic_cloud_download
        )
    }
    AnimatedVisibility(visible = !state.hasSelection) {
        GalleryDisplayActionButton(
            onChange = { action(ChangeDisplay(it as PredefinedGalleryDisplay)) },
            currentGalleryDisplay = state.galleryState.galleryDisplay
        )
    }
}