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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.staggered.StaggeredGridScope
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.group.state.CollapsibleGroupState

@Composable
internal fun StaggeredGridScope.SuperGroup(
    groupState: CollapsibleGroupState,
    content: @Composable ColumnScope.() -> Unit,
) {
    item {
        Card(
            shape = MaterialTheme.shapes.small,
            elevation = 2.dp,
        ) {
            Column {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { groupState.toggleCollapsed() }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = stringResource(groupState.title),
                            style = MaterialTheme.typography.h6,
                        )
                        Icon(
                            painter = rememberAnimatedVectorPainter(
                                animatedImageVector = AnimatedImageVector.animatedVectorResource(
                                    drawable.ic_plus_minus),
                                atEnd = !groupState.isCollapsed,
                            ),
                            contentDescription = null,
                        )
                    }
                }
                AnimatedVisibility(visible = !groupState.isCollapsed) {
                    Column {
                        content()
                    }
                }
            }
        }
    }
}