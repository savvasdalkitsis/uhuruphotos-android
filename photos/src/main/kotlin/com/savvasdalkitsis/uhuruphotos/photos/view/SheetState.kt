package com.savvasdalkitsis.uhuruphotos.photos.view

interface SheetState {
    val isHidden: Boolean
    suspend fun hide()
    suspend fun show()
}