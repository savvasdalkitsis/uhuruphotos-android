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
package com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.LibraryColors
import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.seam.actions.AboutAction
import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.seam.actions.Donate
import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.seam.actions.NavigateToGithub
import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.seam.actions.NavigateToPrivacyPolicy
import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.seam.actions.SendFeedback
import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.ui.state.AboutState
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewThemeData
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewThemeDataProvider
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.button.UhuruIconOutlineButton
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruUpNavButton
import my.nanihadesuka.compose.InternalLazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSelectionMode
import my.nanihadesuka.compose.ScrollbarSettings
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_book_open
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_feedback
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_github
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_logo
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_money
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.about
import uhuruphotos_android.foundation.strings.api.generated.resources.donate
import uhuruphotos_android.foundation.strings.api.generated.resources.feedback
import uhuruphotos_android.foundation.strings.api.generated.resources.privacy_policy

@Composable
internal fun About(
    state: AboutState,
    action: (AboutAction) -> Unit,
) {
    UhuruScaffold(
        title = { Text(stringResource(string.about)) },
        navigationIcon = { UhuruUpNavButton() },
    ) { contentPadding ->
        val listState = rememberLazyListState()
        val backgroundColor = MaterialTheme.colorScheme.background
        val contentColor = contentColorFor(MaterialTheme.colorScheme.background)
        val badgeBackgroundColor = MaterialTheme.colorScheme.primary
        val badgeContentColor = contentColorFor(MaterialTheme.colorScheme.primary)
        val dialogConfirmButtonColor = MaterialTheme.colorScheme.primary
        LibrariesContainer(
            modifier = Modifier
                .recomposeHighlighter()
                .fillMaxSize(),
            showLicenseBadges = false,
            contentPadding = contentPadding,
            lazyListState = listState,
            colors = remember {
                object: LibraryColors {
                    override val backgroundColor = backgroundColor
                    override val contentColor = contentColor
                    override val badgeBackgroundColor = badgeBackgroundColor
                    override val badgeContentColor = badgeContentColor
                    override val dialogConfirmButtonColor = dialogConfirmButtonColor
                }
            },
            header = {
                stickyHeader {
                    AboutHeader(state, action)
                }
            }
        )
        Box(modifier = Modifier
            .recomposeHighlighter()
            .padding(contentPadding)
        ) {
            InternalLazyColumnScrollbar(
                state = listState,
                settings = ScrollbarSettings(
                    thumbThickness = 8.dp,
                    selectionMode = ScrollbarSelectionMode.Thumb,
                    thumbSelectedColor = MaterialTheme.colorScheme.primary,
                    thumbUnselectedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                ),
            )
        }
    }
}

@Composable
private fun AboutHeader(
    state: AboutState,
    action: (AboutAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .recomposeHighlighter()
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = spacedBy(8.dp),
    ) {
        Icon(
            modifier = Modifier
                .recomposeHighlighter()
                .size(80.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerLow, CircleShape),
            tint = MaterialTheme.colorScheme.onSurface,
            painter = painterResource(drawable.ic_logo),
            contentDescription = null,
        )
        Text(
            text = "UhuruPhotos",
            style = MaterialTheme.typography.headlineLarge,
        )
        Text(
            text = state.appVersion,
            style = MaterialTheme.typography.bodyLarge,
        )
        Row(
            modifier = Modifier
                .recomposeHighlighter()
                .fillMaxWidth(),
            horizontalArrangement = spacedBy(8.dp),
        ) {
            UhuruIconOutlineButton(
                modifier = Modifier
                    .recomposeHighlighter()
                    .weight(1f),
                icon = drawable.ic_github,
                text = "Github",
                onClick = { action(NavigateToGithub ) },
            )
            UhuruIconOutlineButton(
                modifier = Modifier
                    .recomposeHighlighter()
                    .weight(1f),
                icon = drawable.ic_money,
                text = stringResource(string.donate),
                onClick = { action(Donate) },
            )
        }
        Row(
            modifier = Modifier
                .recomposeHighlighter()
                .fillMaxWidth(),
            horizontalArrangement = spacedBy(8.dp),
        ) {
            UhuruIconOutlineButton(
                modifier = Modifier
                    .recomposeHighlighter()
                    .weight(1f),
                icon = drawable.ic_feedback,
                text = stringResource(string.feedback),
                onClick = { action(SendFeedback) },
            )
            UhuruIconOutlineButton(
                modifier = Modifier.weight(1f),
                icon = drawable.ic_book_open,
                onClick = { action(NavigateToPrivacyPolicy) },
                text = stringResource(string.privacy_policy)
            )
        }
    }
}

@Preview
@Composable
private fun AboutHeaderPreview(@PreviewParameter(PreviewThemeDataProvider::class) data: PreviewThemeData) {
    PreviewAppTheme(
        themeMode = data.themeMode,
        theme = data.themeVariant,
    ) {
        About(AboutState("0.0.999")) {}
    }
}