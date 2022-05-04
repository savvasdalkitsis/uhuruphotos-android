package com.savvasdalkitsis.uhuruphotos.feedpage.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction
import com.savvasdalkitsis.uhuruphotos.feedpage.view.state.FeedPageState
import com.savvasdalkitsis.uhuruphotos.icons.R
import com.savvasdalkitsis.uhuruphotos.ui.view.ActionIcon
import com.savvasdalkitsis.uhuruphotos.ui.view.Logo

@Composable
fun FeedPageTitle(
    state: FeedPageState,
    action: (FeedPageAction) -> Unit,
    scrollToTop: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Logo(
            onClick = { scrollToTop() }
        )
        AnimatedVisibility(visible = state.hasSelection) {
            OutlinedButton(
                modifier = Modifier
                    .heightIn(max = 48.dp),
                contentPadding = PaddingValues(2.dp),
                onClick = { action(FeedPageAction.ClearSelected) },
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = state.selectedPhotoCount.toString(),
                )
                ActionIcon(
                    modifier = Modifier.size(16.dp),
                    onClick = { action(FeedPageAction.ClearSelected) },
                    icon = R.drawable.ic_clear
                )
            }
        }
    }
}