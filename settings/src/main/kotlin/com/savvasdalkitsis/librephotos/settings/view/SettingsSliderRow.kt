
package com.savvasdalkitsis.librephotos.settings.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsSliderRow(
    text: String,
    subtext: String? = null,
    value: Float?,
    range: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .heightIn(min = 64.dp)
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Text(text)
        Slider(
            enabled = value != null,
            modifier = Modifier
                .fillMaxWidth(),
            value = value ?: range.start,
            valueRange = range,
            steps = steps,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
        )
        if (subtext != null) {
            Text(subtext, style = MaterialTheme.typography.subtitle2)
        }
    }
}