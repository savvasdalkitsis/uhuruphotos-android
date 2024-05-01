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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.DynamicIcon

@Composable
fun TextWithIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier
        .size(24.dp),
    textModifier: Modifier = Modifier,
    icon: Int,
    tint: Color? = null,
    style: TextStyle = LocalTextStyle.current,
    verticalAlignment: Alignment.Vertical = CenterVertically,
    animateIfAvailable: Boolean = true,
    text: String,
) {
    Row(
        modifier = modifier
            .recomposeHighlighter()
        ,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = verticalAlignment,
    ) {
        DynamicIcon(
            modifier = iconModifier
                .recomposeHighlighter(),
            icon = icon,
            tint = tint,
            animateIfAvailable = animateIfAvailable,
            contentDescription = null
        )
        Text(
            modifier = textModifier
                .recomposeHighlighter(),
            text = text,
            style = style,
        )
    }
}