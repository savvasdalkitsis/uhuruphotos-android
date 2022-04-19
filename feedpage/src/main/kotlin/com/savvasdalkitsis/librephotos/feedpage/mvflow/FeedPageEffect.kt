package com.savvasdalkitsis.librephotos.feedpage.mvflow

import androidx.compose.ui.geometry.Offset
import com.savvasdalkitsis.librephotos.photos.model.Photo

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
}
