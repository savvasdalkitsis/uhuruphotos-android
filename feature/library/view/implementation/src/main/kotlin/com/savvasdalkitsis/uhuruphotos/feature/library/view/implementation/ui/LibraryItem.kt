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
package com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.Vitrine
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.CustomColors

@Composable
internal fun LibraryItem(
    modifier: Modifier = Modifier,
    state: VitrineState,
    photoGridModifier: Modifier,
    @DrawableRes iconFallback: Int,
    title: String,
    onSelected: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(8.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.isEmpty && iconFallback != -1) Image(
                modifier = photoGridModifier
                    .clip(MaterialTheme.shapes.large)
                    .background(CustomColors.emptyItem)
                    .clickable { onSelected() }
                    .padding(24.dp),
                painter = painterResource(iconFallback),
                contentDescription = null,
            ) else Vitrine(
                modifier = photoGridModifier,
                state = state,
                onSelected = onSelected,
                shape = MaterialTheme.shapes.large
            )
        }
        LibrarySubtitle(title)
    }
}