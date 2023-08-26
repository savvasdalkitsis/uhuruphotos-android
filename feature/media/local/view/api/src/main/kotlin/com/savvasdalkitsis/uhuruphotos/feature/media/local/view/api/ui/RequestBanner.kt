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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.view.api.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

@Composable
fun RequestBanner(
    modifier: Modifier = Modifier,
    @StringRes description: Int,
    @StringRes grantText: Int = string.grant_permissions,
    onAccessGranted: () -> Unit,
    onNeverRemindMeAgain: () -> Unit,
) {
    Card(
        modifier = modifier
            .recomposeHighlighter()
        ,
    ) {
        Column(
            modifier = Modifier
                .recomposeHighlighter()
                .fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier
                    .recomposeHighlighter()
                    .fillMaxWidth()
                    .padding(8.dp),
                text = stringResource(description),
                textAlign = TextAlign.Center,
            )
            Row(
                modifier = Modifier
                    .recomposeHighlighter()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    modifier = Modifier
                        .recomposeHighlighter()
                        .fillMaxWidth()
                        .weight(1f),
                    onClick = onAccessGranted,
                ) {
                    Text(stringResource(grantText))
                }
                Button(
                    modifier = Modifier
                        .recomposeHighlighter()
                        .fillMaxWidth()
                        .weight(1f),
                    onClick = onNeverRemindMeAgain
                ) {
                    Text(stringResource(string.do_not_ask_again))
                }
            }
        }
    }
}