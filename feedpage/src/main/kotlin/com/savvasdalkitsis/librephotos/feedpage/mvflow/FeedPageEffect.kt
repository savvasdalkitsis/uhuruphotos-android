package com.savvasdalkitsis.librephotos.feedpage.mvflow

import androidx.compose.ui.geometry.Offset

sealed class FeedPageEffect {
    data class OpenPhotoDetails(
        val id: String,
        val center: Offset,
        val scale: Float,
        val isVideo: Boolean,
    ) : FeedPageEffect()
    object ReloadApp : FeedPageEffect()
}
