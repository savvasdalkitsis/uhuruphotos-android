/*
Copyright 2023 Savvas Dalkitsis

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
@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.SharedElementId
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.sharedElement
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import org.jetbrains.compose.resources.DrawableResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_airplane

@Composable
fun SharedTransitionScope.ActionRowWithIcon(
    modifier: Modifier = Modifier,
    icon: DrawableResource,
    text: String,
    sharedElementId: SharedElementId? = null,
    textSharedElementId: SharedElementId? = null,
    onClick: () -> Unit,
) {
    TextWithIcon(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .clickable { onClick() }
            .padding(6.dp)
            .fillMaxWidth()
            .then(
                if (sharedElementId != null) {
                    Modifier.sharedElement(sharedElementId)
                } else {
                    Modifier
                }
            ),
        iconModifier = Modifier
            .size(28.dp),
        textModifier = Modifier
            .fillMaxWidth()
            .then(
                if (textSharedElementId != null) {
                    Modifier.sharedElement(textSharedElementId)
                } else {
                    Modifier
                }
            ),
        icon = icon,
        text = text,
    )
}

@Preview
@Composable
private fun ActionRowWithIconPreview() {
    PreviewAppTheme {
        SharedTransitionLayout {
            ActionRowWithIcon(icon = drawable.ic_airplane, text = "Some text") {}
        }
    }
}