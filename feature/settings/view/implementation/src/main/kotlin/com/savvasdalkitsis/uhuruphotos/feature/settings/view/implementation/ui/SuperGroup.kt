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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.controller.SettingsGroupState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.StaggeredGridScope

@Composable
internal fun StaggeredGridScope.SuperGroup(
    groupState: SettingsGroupState,
    content: @Composable ColumnScope.() -> Unit,
) {
    item {
        Surface(
            modifier = Modifier,
            shadowElevation = 2.dp,
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
                            style = MaterialTheme.typography.labelMedium,
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