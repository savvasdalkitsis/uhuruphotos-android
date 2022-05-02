package com.savvasdalkitsis.uhuruphotos.feed.view.state

import com.savvasdalkitsis.uhuruphotos.ui.window.WindowSizeClass

interface FeedDisplay {
    val compactColumnsPortrait: Int
    val compactColumnsLandscape: Int
    val wideColumnsPortrait: Int
    val wideColumnsLandscape: Int
    val shouldAddEmptyPhotosInRows: Boolean
    val iconResource: Int
    val maintainAspectRatio: Boolean
    val friendlyName: String
    val zoomIn: FeedDisplay
    val zoomOut: FeedDisplay

    fun columnCount(
        windowSizeClass: WindowSizeClass,
        landscape: Boolean,
    ) = when {
        landscape -> when (windowSizeClass) {
            WindowSizeClass.COMPACT -> compactColumnsLandscape
            else -> wideColumnsLandscape
        }
        else -> when (windowSizeClass) {
            WindowSizeClass.COMPACT -> compactColumnsPortrait
            else -> wideColumnsPortrait
        }
    }
}