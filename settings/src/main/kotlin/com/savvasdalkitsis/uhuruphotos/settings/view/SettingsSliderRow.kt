
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
    text: (Float) -> String,
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