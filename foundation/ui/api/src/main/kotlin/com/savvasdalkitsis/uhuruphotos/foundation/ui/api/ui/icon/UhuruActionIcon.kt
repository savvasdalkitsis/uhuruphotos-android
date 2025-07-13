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

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.animation.AnimationResource
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_feed

@Composable
fun UhuruActionIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color? = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit,
    icon: Either<DrawableResource, AnimationResource>,
    contentDescription: String? = null
) {
    when (icon) {
        is Either.Left<DrawableResource, *> -> UhuruActionIcon(modifier, iconModifier, enabled, tint, onClick, icon.value, contentDescription)
        is Either.Right<*, AnimationResource> -> UhuruActionIcon(modifier, iconModifier, enabled, tint, onClick, icon.value, contentDescription)
    }
}
@Composable
fun UhuruActionIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color? = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit,
    icon: DrawableResource,
    contentDescription: String? = null
) {
    UhuruActionIcon(modifier, iconModifier, enabled, tint, onClick, painterResource(icon), contentDescription)
}

@Composable
fun UhuruActionIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color? = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit,
    painter: Painter,
    contentDescription: String? = null
) {
    UhuruActionIcon(
        modifier = modifier
            .recomposeHighlighter()
        ,
        onClick = onClick,
        enabled = enabled,
    ) {
        UhuruIcon(
            modifier = iconModifier
                .recomposeHighlighter()
                .sizeIn(maxWidth = 26.dp, maxHeight = 26.dp),
            painter = painter,
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}

@Composable
fun UhuruActionIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color? = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit,
    animation: AnimationResource,
    contentDescription: String? = null
) {
    UhuruActionIcon(
        modifier = modifier
            .recomposeHighlighter()
        ,
        onClick = onClick,
        enabled = enabled,
    ) {
        UhuruIcon(
            modifier = iconModifier
                .recomposeHighlighter()
                .sizeIn(maxWidth = 26.dp, maxHeight = 26.dp),
            icon = animation,
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}

@Composable
fun UhuruActionIcon(
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
        UhuruActionIcon(
            onClick = { },
            icon = drawable.ic_feed,
        )
    }
}