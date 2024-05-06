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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.ImageResource

@Composable
fun ActionRowWithIcon(
    modifier: Modifier = Modifier,
    icon: ImageResource,
    text: String,
    onClick: () -> Unit,
) {
    TextWithIcon(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .clickable { onClick() }
            .padding(6.dp)
            .fillMaxWidth(),
        iconModifier = Modifier
            .size(28.dp),
        textModifier = Modifier.fillMaxWidth(),
        icon = icon,
        text = text,
    )
}