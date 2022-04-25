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
}
