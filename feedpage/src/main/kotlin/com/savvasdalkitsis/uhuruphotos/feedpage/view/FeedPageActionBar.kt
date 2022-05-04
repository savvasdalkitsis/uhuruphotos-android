package com.savvasdalkitsis.uhuruphotos.feedpage.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction
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
            onClick = { action(FeedPageAction.ShareSelectedPhotos) },
            icon = R.drawable.ic_share
        )
    }
    AnimatedVisibility(visible = state.hasSelection) {
        ActionIcon(
            onClick = { action(FeedPageAction.AskForSelectedPhotosDeletion) },
            icon = R.drawable.ic_delete
        )
    }
    FeedDisplayActionButton(
        onShow = { action(FeedPageAction.ShowFeedDisplayChoice) },
        onHide = { action(FeedPageAction.HideFeedDisplayChoice) },
        onChange = { action(FeedPageAction.ChangeDisplay(it as FeedDisplays)) },
        expanded = state.showFeedDisplayChoice,
        currentFeedDisplay = state.feedState.feedDisplay
    )
}