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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.animation.AnimationResource
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewThemeData
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewThemeDataProvider
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruIcon
import org.jetbrains.compose.resources.DrawableResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_airplane

@Composable
fun TextWithIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier
        .size(24.dp),
    textModifier: Modifier = Modifier,
    icon: AnimationResource,
    tint: Color? = null,
    style: TextStyle = LocalTextStyle.current,
    verticalAlignment: Alignment.Vertical = CenterVertically,
    animateIfAvailable: Boolean = true,
    enabled: Boolean = true,
    text: String,
) {
    TextWithIcon(
        modifier,
        textModifier,
        iconSlot = {
            UhuruIcon(
                modifier = iconModifier
                    .recomposeHighlighter(),
                icon = icon,
                tint = if (enabled)
                    tint
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                animateIfAvailable = animateIfAvailable,
                contentDescription = null
            )

        },
        style = style,
        verticalAlignment = verticalAlignment,
        enabled = enabled,
        text = text,
    )
}

@Composable
fun TextWithIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier
        .size(24.dp),
    textModifier: Modifier = Modifier,
    icon: DrawableResource,
    tint: Color? = null,
    style: TextStyle = LocalTextStyle.current,
    verticalAlignment: Alignment.Vertical = CenterVertically,
    animateIfAvailable: Boolean = true,
    enabled: Boolean = true,
    text: String,
) {
    TextWithIcon(
        modifier,
        textModifier,
        iconSlot = {
            UhuruIcon(
                modifier = iconModifier
                    .recomposeHighlighter(),
                icon = icon,
                tint = if (enabled)
                    tint
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                animateIfAvailable = animateIfAvailable,
                contentDescription = null
            )

        },
        style = style,
        verticalAlignment = verticalAlignment,
        enabled = enabled,
        text = text,
    )
}

@Composable
private fun TextWithIcon(
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    iconSlot: @Composable() (RowScope.() -> Unit),
    style: TextStyle = LocalTextStyle.current,
    verticalAlignment: Alignment.Vertical = CenterVertically,
    enabled: Boolean = true,
    text: String,
) {
    Row(
        modifier = modifier
            .recomposeHighlighter()
        ,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = verticalAlignment,
    ) {
        iconSlot()
        Text(
            modifier = textModifier
                .recomposeHighlighter(),
            text = text,
            color = if (enabled)
                Color.Unspecified
            else
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            style = style,
        )
    }
}

@Preview
@Composable
private fun TextWithIconPreview(@PreviewParameter(PreviewThemeDataProvider::class) data: PreviewThemeData) {
    PreviewAppTheme(
        themeMode = data.themeMode,
        theme = data.themeVariant,
    ) {
        TextWithIcon(icon = drawable.ic_airplane, text = "Some text")
    }
}
@Preview
@Composable
private fun TextWithIconDisabledPreview(@PreviewParameter(PreviewThemeDataProvider::class) data: PreviewThemeData) {
    PreviewAppTheme(
        themeMode = data.themeMode,
        theme = data.themeVariant,
    ) {
        TextWithIcon(
            icon = drawable.ic_airplane,
            text = "Some text",
            enabled = false,
        )
    }
}