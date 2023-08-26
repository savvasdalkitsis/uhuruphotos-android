package com.savvasdalkitsis.uhuruphotos.feature.stats.view.implementation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollapsibleGroup
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.state.rememberCollapsibleGroupState

@Composable
fun StatsGroup(
    title: Int,
    uniqueId: String,
    isLoading: Boolean,
    content: @Composable () -> Unit,
) {
    val groupState = rememberCollapsibleGroupState(title, "stats_media_per_$uniqueId")
    CollapsibleGroup(groupState = groupState) {
        AnimatedContent(targetState = isLoading, label = "") { loading ->
            when {
                loading -> Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(minHeight = 240.dp),
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }

                else -> content()
            }
        }
    }
}