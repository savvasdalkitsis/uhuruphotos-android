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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.animation.AnimationResource
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruIcon
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.no_media

@Composable
fun NoContent(
    message: StringResource,
    refreshable: Boolean = true,
    additionalContent: (@Composable () -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .recomposeHighlighter()
            .fillMaxSize()
            .then(if (refreshable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            UhuruIcon(
                modifier = Modifier.size(128.dp),
                icon = AnimationResource.animation_empty,
            )
            Text(
                text = stringResource(message),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge
                    .copy(fontWeight = FontWeight.Bold),
            )
            additionalContent?.invoke()
        }
    }
}

@Preview
@Composable
private fun NoContentPreview() {
    PreviewAppTheme {
        NoContent(message = string.no_media)
    }
}