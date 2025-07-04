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
package com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.animation.AnimationResource
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.ContentTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.CustomColors
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.TextWithIcon
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_checkbox_selected
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_checkbox_unselected
import uhuruphotos_android.foundation.strings.api.generated.resources.Res
import uhuruphotos_android.foundation.strings.api.generated.resources.start

@Composable
internal fun RowScope.WelcomeUseCase(
    icon: AnimationResource,
    text: StringResource,
    selected: Boolean,
    tint: Color? = null,
    onClick: () -> Unit,
) {
    ContentTheme(themeMode = ThemeMode.LIGHT_MODE) {
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            elevation = CardDefaults.elevatedCardElevation(),
            shape = CardDefaults.elevatedShape,
        ) {
            ConstraintLayout(modifier = Modifier
                .fillMaxHeight()
                .clickable(enabled = !selected) { onClick() }
                .padding(8.dp)
            ) {
                val (iconC, textC) = createRefs()

                TextWithIcon(
                    modifier = Modifier.constrainAs(textC) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                    iconModifier = Modifier.size(24.dp),
                    icon = if (selected) drawable.ic_checkbox_selected else drawable.ic_checkbox_unselected,
                    tint = if (selected) CustomColors.selected else null,
                    text = stringResource(text),
                )
                UhuruIcon(
                    modifier = Modifier.constrainAs(iconC) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(textC.top)
                    },
                    tint = tint,
                    icon = icon,
                )
            }
        }
    }
}

// preview
@Composable
@Preview
private fun WelcomeUseCasePreview() {
    PreviewAppTheme {
        Row {
            WelcomeUseCase(
                icon = AnimationResource.animation_cloud,
                text = Res.string.start,
                selected = false,
                onClick = {},
            )
        }
    }
}