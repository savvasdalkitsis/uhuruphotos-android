package com.savvasdalkitsis.librephotos.photos.view

import androidx.compose.runtime.compositionLocalOf

enum class PhotoSheetStyle {
    BOTTOM, SIDE
}
val LocalPhotoSheetStyle = compositionLocalOf<PhotoSheetStyle> {
    throw IllegalStateException("not set")
}