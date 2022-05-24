package com.savvasdalkitsis.uhuruphotos.feedpage.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feed.view.FeedDisplayActionButton
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.AskForSelectedPhotosDeletion
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.ShareSelectedPhotos
import com.savvasdalkitsis.uhuruphotos.feedpage.view.state.FeedPageState
import com.savvasdalkitsis.uhuruphotos.icons.R
import com.savvasdalkitsis.uhuruphotos.ui.view.ActionIcon

@Composable
fun RowScope.FeedPageActionBar(
    state: FeedPageState,
    action: (FeedPageAction) -> Unit
) {
    AnimatedVisibility(visible = state.shouldShowShareIcon) {
        ActionIcon(
            onClick = { action(ShareSelectedPhotos) },
            icon = R.drawable.ic_share
        )
    }
    AnimatedVisibility(visible = state.hasSelection) {
        ActionIcon(
            onClick = { action(AskForSelectedPhotosDeletion) },
            icon = R.drawable.ic_delete
        )
    }
    FeedDisplayActionButton(
        onChange = { action(ChangeDisplay(it as FeedDisplays)) },
        currentFeedDisplay = state.feedState.feedDisplay
    )
}