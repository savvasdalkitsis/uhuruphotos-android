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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam

import androidx.compose.ui.geometry.Offset
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.Cluster
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState

internal sealed class FeedAction {
    data class SelectedCel(
        val celState: CelState,
        val center: Offset,
        val scale: Float,
    ) : FeedAction()
    data class ChangeDisplay(val display: PredefinedCollageDisplay) : FeedAction()
    data class CelLongPressed(val celState: CelState) : FeedAction()
    data class ClusterSelectionClicked(val cluster: Cluster) : FeedAction()
    data class ClusterRefreshClicked(val cluster: Cluster) : FeedAction()
    data class MemorySelected(val memoryCel: CelState, val center: Offset, val scale: Float) : FeedAction()

    object LoadFeed : FeedAction()
    object RefreshFeed : FeedAction()
    object ClearSelected : FeedAction()
    object AskForSelectedPhotosTrashing : FeedAction()
    object DismissSelectedPhotosTrashing : FeedAction()
    object TrashSelectedCels : FeedAction()
    object ShareSelectedCels : FeedAction()
    object DownloadSelectedCels : FeedAction()
    object DismissLocalMediaAccessPermissionRequest : FeedAction()
}
