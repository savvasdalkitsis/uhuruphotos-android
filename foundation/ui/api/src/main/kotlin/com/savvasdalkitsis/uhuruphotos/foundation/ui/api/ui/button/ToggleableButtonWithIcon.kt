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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.animation.AnimationResource
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.copy
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewThemeData
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewThemeDataProvider
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.TextWithIcon
import org.jetbrains.compose.resources.DrawableResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_info

@Composable
fun ToggleableButtonWithIcon(
    modifier: Modifier = Modifier,
    icon: AnimationResource,
    iconModifier: Modifier = Modifier,
    iconTint: Color? = null,
    animateIfAvailable: Boolean = true,
    enabled: Boolean = true,
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ToggleableButtonWithIcon(
        modifier = modifier,
        iconSlot = {
            TextWithIcon(
                modifier = Modifier.weight(1f),
                iconModifier = iconModifier,
                icon = icon,
                tint = iconTint,
                text = text,
                enabled = enabled,
                animateIfAvailable = animateIfAvailable,
            )
        },
        iconModifier = iconModifier,
        iconTint = iconTint,
        animateIfAvailable = animateIfAvailable,
        enabled = enabled,
        text = text,
        checked = checked,
        onCheckedChange = onCheckedChange,
    )
}

@Composable
fun ToggleableButtonWithIcon(
    modifier: Modifier = Modifier,
    icon: DrawableResource,
    iconModifier: Modifier = Modifier,
    iconTint: Color? = null,
    animateIfAvailable: Boolean = true,
    enabled: Boolean = true,
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ToggleableButtonWithIcon(
        modifier = modifier,
        iconSlot = {
            TextWithIcon(
                modifier = Modifier.weight(1f),
                iconModifier = iconModifier,
                icon = icon,
                tint = iconTint,
                text = text,
                enabled = enabled,
                animateIfAvailable = animateIfAvailable,
            )
        },
        iconModifier = iconModifier,
        iconTint = iconTint,
        animateIfAvailable = animateIfAvailable,
        enabled = enabled,
        text = text,
        checked = checked,
        onCheckedChange = onCheckedChange,
    )
}

@Composable
private fun ToggleableButtonWithIcon(
    modifier: Modifier = Modifier,
    iconSlot: @Composable RowScope.() -> Unit,
    iconModifier: Modifier = Modifier,
    iconTint: Color? = null,
    animateIfAvailable: Boolean = true,
    enabled: Boolean = true,
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    OutlinedButton(
        modifier = modifier,
        enabled = enabled,
        onClick = { onCheckedChange(!checked) },
        contentPadding = ButtonDefaults.ContentPadding.copy(top = 0.dp, bottom = 0.dp, end = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            iconSlot()
            if (enabled) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                )
            }
        }
    }
}

@Preview
@Composable
private fun ToggleableButtonWithIconPreview(@PreviewParameter(PreviewThemeDataProvider::class) data: PreviewThemeData) {
    PreviewAppTheme(
        theme = data.themeVariant,
        themeMode = data.themeMode,
    ) {
        Box(Modifier.padding(8.dp)) {
            ToggleableButtonWithIcon(
                modifier = Modifier.fillMaxWidth(),
                icon = drawable.ic_info,
                text = "Info",
                checked = true,
            ) {}
        }
    }
}

@Preview
@Composable
private fun ToggleableButtonWithIconUnselectedPreview(@PreviewParameter(PreviewThemeDataProvider::class) data: PreviewThemeData) {
    PreviewAppTheme(
        theme = data.themeVariant,
        themeMode = data.themeMode,
    ) {
        Box(Modifier.padding(8.dp)) {
            ToggleableButtonWithIcon(
                modifier = Modifier.fillMaxWidth(),
                icon = drawable.ic_info,
                text = "Info",
                checked = false,
            ) {}
        }
    }
}

@Preview
@Composable
private fun ToggleableButtonWithIconDisabledPreview(@PreviewParameter(PreviewThemeDataProvider::class) data: PreviewThemeData) {
    PreviewAppTheme(
        theme = data.themeVariant,
        themeMode = data.themeMode,
    ) {
        Box(Modifier.padding(8.dp)) {
            ToggleableButtonWithIcon(
                modifier = Modifier.fillMaxWidth(),
                icon = drawable.ic_info,
                text = "Info",
                checked = false,
                enabled = false,
            ) {}
        }
    }
}