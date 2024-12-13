package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkbox

import androidx.annotation.DrawableRes
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
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruIcon

@Composable
fun UhuruCheckBoxRow(
    text: String,
    @DrawableRes icon: Int,
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
        UhuruCheckBoxRow(text = "Setting", icon = R.drawable.ic_info, isChecked = true) {}
    }
}

@Preview
@Composable
private fun SettingsCheckBoxPreviewDark() {
    PreviewAppTheme(themeMode = ThemeMode.DARK_MODE) {
        UhuruCheckBoxRow(text = "Setting", icon = R.drawable.ic_info, isChecked = true) {}
    }
}

@Preview
@Composable
private fun SettingsCheckBoxUnselectedPreview() {
    PreviewAppTheme {
        UhuruCheckBoxRow(text = "Setting", icon = R.drawable.ic_info, isChecked = false) {}
    }
}

@Preview
@Composable
private fun SettingsCheckBoxUnselectedPreviewDark() {
    PreviewAppTheme(themeMode = ThemeMode.DARK_MODE) {
        UhuruCheckBoxRow(text = "Setting", icon = R.drawable.ic_info, isChecked = false) {}
    }
}