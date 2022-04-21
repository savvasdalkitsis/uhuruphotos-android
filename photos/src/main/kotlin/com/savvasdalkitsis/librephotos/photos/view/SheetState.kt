package com.savvasdalkitsis.librephotos.photos.view

interface SheetState {
    val isHidden: Boolean
    suspend fun hide()
    suspend fun show()
}