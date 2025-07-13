/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.CustomColors
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import org.jetbrains.compose.resources.DrawableResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_feed
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_outline_selected
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_outline_unselected

@Composable
fun UhuruToggleableActionIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    icon: DrawableResource,
    selected: Boolean,
    contentDescription: String? = null
) {
    UhuruActionIcon(
        modifier = modifier
            .recomposeHighlighter(),
        onClick = onClick,
        enabled = enabled,
    ) {
        Box {
            UhuruIcon(
                modifier = iconModifier
                    .sizeIn(maxWidth = 26.dp, maxHeight = 26.dp),
                icon = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onBackground
            )
            UhuruIcon(
                modifier = iconModifier
                    .sizeIn(maxWidth = 12.dp, maxHeight = 12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background)
                    .align(Alignment.BottomEnd),
                icon = if (selected)
                    drawable.ic_outline_selected
                else
                    drawable.ic_outline_unselected,
                contentDescription = contentDescription,
                tint = if (selected)
                    CustomColors.selected
                else
                    MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview
@Composable
private fun ToggleableActionIconSelectedPreview() {
    PreviewAppTheme {
        UhuruToggleableActionIcon(
            onClick = { },
            icon = drawable.ic_feed,
            selected = true,
        )
    }
}

@Preview
@Composable
private fun ToggleableActionIconUnselectedPreview() {
    PreviewAppTheme {
        UhuruToggleableActionIcon(
            onClick = { },
            icon = drawable.ic_feed,
            selected = false,
        )
    }
}