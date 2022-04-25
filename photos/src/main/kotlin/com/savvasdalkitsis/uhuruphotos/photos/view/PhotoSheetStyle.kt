package com.savvasdalkitsis.uhuruphotos.photos.view

import androidx.compose.runtime.compositionLocalOf

enum class PhotoSheetStyle {
    BOTTOM, SIDE
}
val LocalPhotoSheetStyle = compositionLocalOf<PhotoSheetStyle> {
    throw IllegalStateException("not set")
}