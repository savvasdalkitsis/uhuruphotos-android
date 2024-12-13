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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.slider

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.layout.UhuruEntryWithSubtext
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.UhuruTextRow

@Composable
fun UhuruSliderRow(
    text: @Composable (Float) -> String,
    @StringRes subtext: Int? = null,
    initialValue: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    onValueChanged: (Float) -> Unit = {},
) {
    UhuruEntryWithSubtext(
        subtext = subtext,
    ) {
        var sliderValue by remember(initialValue) { mutableFloatStateOf(initialValue) }
        UhuruTextRow(text(sliderValue))
        Slider(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            value = sliderValue,
            valueRange = range,
            steps = steps,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { onValueChanged(sliderValue) },
        )
    }
}