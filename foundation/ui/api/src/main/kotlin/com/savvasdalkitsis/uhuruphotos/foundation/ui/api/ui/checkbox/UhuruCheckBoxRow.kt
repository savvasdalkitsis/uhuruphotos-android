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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkbox

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruIcon
import org.jetbrains.compose.resources.DrawableResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_info

@Composable
fun UhuruCheckBoxRow(
    text: String,
    icon: DrawableResource,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onCheckedChange(!isChecked) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        UhuruIcon(icon = icon)
        Text(
            modifier = Modifier.weight(1f),
            text = text,
        )
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Preview
@Composable
private fun SettingsCheckBoxPreview() {
    PreviewAppTheme {
        UhuruCheckBoxRow(text = "Setting", icon = drawable.ic_info, isChecked = true) {}
    }
}

@Preview
@Composable
private fun SettingsCheckBoxPreviewDark() {
    PreviewAppTheme(themeMode = ThemeMode.DARK_MODE) {
        UhuruCheckBoxRow(text = "Setting", icon = drawable.ic_info, isChecked = true) {}
    }
}

@Preview
@Composable
private fun SettingsCheckBoxUnselectedPreview() {
    PreviewAppTheme {
        UhuruCheckBoxRow(text = "Setting", icon = drawable.ic_info, isChecked = false) {}
    }
}

@Preview
@Composable
private fun SettingsCheckBoxUnselectedPreviewDark() {
    PreviewAppTheme(themeMode = ThemeMode.DARK_MODE) {
        UhuruCheckBoxRow(text = "Setting", icon = drawable.ic_info, isChecked = false) {}
    }
}