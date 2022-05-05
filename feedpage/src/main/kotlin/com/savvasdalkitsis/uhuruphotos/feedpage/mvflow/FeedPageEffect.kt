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
package com.savvasdalkitsis.uhuruphotos.feedpage.mvflow

import androidx.compose.ui.geometry.Offset
import com.savvasdalkitsis.uhuruphotos.photos.model.Photo

sealed class FeedPageEffect {
    data class OpenPhotoDetails(
        val id: String,
        val center: Offset,
        val scale: Float,
        val isVideo: Boolean,
    ) : FeedPageEffect()

    data class SharePhotos(val selectedPhotos: List<Photo>) : FeedPageEffect()
    object ReloadApp : FeedPageEffect()
    object NavigateToServerEdit : FeedPageEffect()
    object Vibrate : FeedPageEffect()
    object NavigateToSettings : FeedPageEffect()
    object SendFeedback : FeedPageEffect()
}
