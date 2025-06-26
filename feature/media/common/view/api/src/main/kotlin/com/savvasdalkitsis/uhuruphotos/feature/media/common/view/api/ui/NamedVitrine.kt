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
@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.CustomColors
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun SharedTransitionScope.NamedVitrine(
    modifier: Modifier = Modifier,
    state: VitrineState,
    photoGridModifier: Modifier,
    iconFallback: DrawableResource?,
    title: String,
    selectable: Boolean = true,
    onSelected: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(8.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (state.isEmpty && iconFallback != null) Image(
                modifier = photoGridModifier
                    .clip(CardDefaults.shape)
                    .background(CustomColors.emptyItem)
                    .let { if (selectable) it.clickable { onSelected() } else it }
                    .padding(24.dp),
                painter = painterResource(iconFallback),
                contentDescription = null,
            ) else Vitrine(
                modifier = photoGridModifier,
                state = state,
                selectable = selectable,
                onSelected = onSelected,
                shape = CardDefaults.shape
            )
        }
        VitrineSubtitle(title)
    }
}

@Composable
fun ColumnScope.VitrineSubtitle(text: String) {
    val typography = MaterialTheme.typography
    Text(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(4.dp),
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = remember {
            typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        },
    )
}