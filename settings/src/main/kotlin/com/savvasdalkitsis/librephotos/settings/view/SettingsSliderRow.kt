
package com.savvasdalkitsis.librephotos.settings.view

import android.util.Range
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsSliderRow(
    text: String,
    subtext: String? = null,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
) {
    Column(
        modifier = Modifier
            .heightIn(min = 64.dp)
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Text(text)
        Slider(
            modifier = Modifier
                .fillMaxWidth(),
            value = value,
            valueRange = range,
            onValueChange = onValueChange,
        )
        if (subtext != null) {
            Text(subtext, style = MaterialTheme.typography.subtitle2)
        }
    }
}