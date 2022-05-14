
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
package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsSliderRow(
    text: @Composable (Float) -> String,
    subtext: String? = null,
    initialValue: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    onValueChanged: (Float) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .heightIn(min = 64.dp)
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        var sliderValue by remember { mutableStateOf(initialValue) }
        Text(text(sliderValue))
        Slider(
            modifier = Modifier
                .fillMaxWidth(),
            value = sliderValue,
            valueRange = range,
            steps = steps,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { onValueChanged(sliderValue) },
        )
        if (subtext != null) {
            Text(subtext, style = MaterialTheme.typography.subtitle2)
        }
    }
}