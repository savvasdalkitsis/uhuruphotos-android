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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.android

import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.ActionIcon

@Composable
fun ActionIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    icon: Drawable,
    contentDescription: String? = null
) {
    ActionIcon(
        modifier = modifier
            .recomposeHighlighter(),
        onClick = onClick,
        enabled = enabled,
    ) {
        Icon(
            modifier = iconModifier
                .recomposeHighlighter()
                .sizeIn(maxWidth = 26.dp, maxHeight = 26.dp),
            painter = rememberDrawablePainter(icon),
            contentDescription = contentDescription,
            tint = Color.Unspecified,
        )
    }
}