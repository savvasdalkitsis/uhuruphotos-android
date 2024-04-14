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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme

@Composable
fun ActionIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color? = MaterialTheme.colors.onBackground,
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    contentDescription: String? = null
) {
    ActionIcon(
        modifier = modifier
            .recomposeHighlighter()
        ,
        onClick = onClick,
        enabled = enabled,
    ) {
        DynamicIcon(
            modifier = iconModifier
                .sizeIn(maxWidth = 26.dp, maxHeight = 26.dp),
            icon = icon,
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}

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
            .recomposeHighlighter()
        ,
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

@Composable
fun ActionIcon(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    IconButton(
        modifier = modifier
            .recomposeHighlighter()
        ,
        onClick = onClick,
        enabled = enabled,
    ) {
        icon()
    }
}

@Preview
@Composable
private fun ActionIconPreview() {
    PreviewAppTheme {
        ActionIcon(
            onClick = { },
            icon = drawable.ic_feed,
        )
    }
}