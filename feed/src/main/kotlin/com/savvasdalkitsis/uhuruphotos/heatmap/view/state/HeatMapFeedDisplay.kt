package com.savvasdalkitsis.uhuruphotos.heatmap.view.state

import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplay

object HeatMapFeedDisplay : FeedDisplay {
    override val compactColumnsPortrait = 5
    override val compactColumnsLandscape = 3
    override val wideColumnsPortrait = 7
    override val wideColumnsLandscape = 3
    override val shouldAddEmptyPhotosInRows = true
    override val iconResource: Int
        get() = throw IllegalStateException("This is not used")
    override val maintainAspectRatio = false
    override val friendlyName = "HeatMap"
    override val zoomIn get() = HeatMapFeedDisplay
    override val zoomOut get() = HeatMapFeedDisplay
}