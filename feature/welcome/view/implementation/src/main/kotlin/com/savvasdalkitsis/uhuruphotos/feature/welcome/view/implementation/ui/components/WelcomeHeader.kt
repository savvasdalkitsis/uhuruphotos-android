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
package com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.more_than_one
import uhuruphotos_android.foundation.strings.api.generated.resources.welcome_description
import uhuruphotos_android.foundation.strings.api.generated.resources.welcome_description_2
import uhuruphotos_android.foundation.strings.api.generated.resources.welcome_to_uhuruphotos
import uhuruphotos_android.foundation.strings.api.generated.resources.what_would_like_to_do

@Composable
internal fun WelcomeHeader() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(string.welcome_to_uhuruphotos),
            style = MaterialTheme.typography.headlineLarge,
        )
        Text(
            text = stringResource(string.welcome_description),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = stringResource(string.welcome_description_2),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(string.what_would_like_to_do),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(string.more_than_one),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}