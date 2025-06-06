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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.ClearSelected
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.FeedAction
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.Logo
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruActionIcon
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_clear
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.feed

@Composable
internal fun FeedTitle(
    action: (FeedAction) -> Unit,
    scrollToTop: () -> Unit,
    hasSelection: Boolean,
    selectedCelCount: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Logo(
            onClick = { scrollToTop() }
        )
        if (!hasSelection) {
            Text(stringResource(string.feed))
        } else {
            OutlinedButton(
                modifier = Modifier
                    .heightIn(max = 48.dp),
                contentPadding = PaddingValues(2.dp),
                onClick = { action(ClearSelected) },
                shape = MaterialTheme.shapes.medium,
            ) {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = selectedCelCount.toString(),
                )
                UhuruActionIcon(
                    modifier = Modifier.size(16.dp),
                    onClick = { action(ClearSelected) },
                    icon = drawable.ic_clear
                )
            }
        }
    }
}