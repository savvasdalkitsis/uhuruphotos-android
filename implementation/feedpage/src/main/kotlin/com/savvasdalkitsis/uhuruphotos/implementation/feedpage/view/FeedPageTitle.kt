/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.implementation.feedpage.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.icons.R.drawable
import com.savvasdalkitsis.uhuruphotos.api.strings.R.string
import com.savvasdalkitsis.uhuruphotos.api.ui.view.ActionIcon
import com.savvasdalkitsis.uhuruphotos.api.ui.view.Logo
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageAction
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.view.state.FeedPageState

@Composable
internal fun FeedPageTitle(
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
        AnimatedContent(targetState = state.hasSelection) { hasSelection ->
            if (!hasSelection) {
                Text(stringResource(string.feed))
            } else {
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
                        icon = drawable.ic_clear
                    )
                }
            }
        }
    }
}