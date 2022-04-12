package com.savvasdalkitsis.librephotos.feed.mvflow

import androidx.compose.ui.geometry.Offset

sealed class FeedPageEffect {
    data class OpenPhotoDetails(val id: String, val center: Offset, val scale: Float) : FeedPageEffect()
    object ReloadApp : FeedPageEffect()
}
