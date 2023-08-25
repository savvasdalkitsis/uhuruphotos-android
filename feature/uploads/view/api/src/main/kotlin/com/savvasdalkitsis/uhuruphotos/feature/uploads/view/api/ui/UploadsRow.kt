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
package com.savvasdalkitsis.uhuruphotos.feature.uploads.view.api.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

@Composable
fun UploadsRow(
    inProgress: Boolean,
    onViewAll: () -> Unit,
) {
    Row(
        modifier = Modifier
            .heightIn(min = 64.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            UploadsProgress(inProgress)
        }
        Button(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically),
            onClick = onViewAll,
        ) {
            Text(
                modifier = Modifier.animateContentSize(),
                text = stringResource(string.view_all),
            )
        }
    }
}

@Composable
private fun UploadsProgress(inProgress: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(modifier = Modifier
            .fillMaxWidth(),
            text = stringResource(string.uploads)
        )
        AnimatedVisibility(visible = inProgress) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                strokeCap = StrokeCap.Round,
            )
        }
    }
}