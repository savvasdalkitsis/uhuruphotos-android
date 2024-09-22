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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.seam.actions.AboutAction
import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.seam.actions.Donate
import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.seam.actions.NavigateToGithub
import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.seam.actions.NavigateToPrivacyPolicy
import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.seam.actions.SendFeedback
import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.ui.state.AboutState
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.button.IconOutlineButton
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UpNavButton
import my.nanihadesuka.compose.InternalLazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSelectionMode

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun About(
    state: AboutState,
    action: (AboutAction) -> Unit,
) {
    CommonScaffold(
        title = { Text(stringResource(string.about)) },
        navigationIcon = { UpNavButton() },
    ) { contentPadding ->
        val listState = rememberLazyListState()
        LibrariesContainer(
            modifier = Modifier
                .recomposeHighlighter()
                .fillMaxSize(),
            contentPadding = contentPadding,
            lazyListState = listState,
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
                listState = listState,
                thickness = 8.dp,
                selectionMode = ScrollbarSelectionMode.Thumb,
                thumbColor = MaterialTheme.colors.primary.copy(alpha = 0.7f),
                thumbSelectedColor = MaterialTheme.colors.primary,
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
            .background(MaterialTheme.colors.background)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = spacedBy(8.dp),
    ) {
        Icon(
            modifier = Modifier
                .recomposeHighlighter()
                .size(80.dp)
                .background(Color.White, CircleShape),
            tint = Color.Black,
            painter = painterResource(drawable.ic_logo),
            contentDescription = null,
        )
        Text(
            text = "UhuruPhotos",
            style = MaterialTheme.typography.h3,
        )
        Text(
            text = state.appVersion,
            style = MaterialTheme.typography.subtitle1,
        )
        Row(
            modifier = Modifier
                .recomposeHighlighter()
                .fillMaxWidth(),
            horizontalArrangement = spacedBy(8.dp),
        ) {
            OutlinedButton(
                modifier = Modifier
                    .recomposeHighlighter()
                    .weight(1f),
                onClick = { action(NavigateToGithub )},
            ) {
                Icon(
                    painter = painterResource(drawable.ic_github),
                    contentDescription = null
                )
                Spacer(modifier = Modifier
                    .recomposeHighlighter()
                    .width(8.dp))
                Text(text = "Github")
            }
            OutlinedButton(
                modifier = Modifier
                    .recomposeHighlighter()
                    .weight(1f),
                onClick = { action(Donate) },
            ) {
                Icon(
                    painter = painterResource(drawable.ic_money),
                    contentDescription = null
                )
                Spacer(modifier = Modifier
                    .recomposeHighlighter()
                    .width(8.dp))
                Text(text = stringResource(string.donate))
            }
        }
        Row(
            modifier = Modifier
                .recomposeHighlighter()
                .fillMaxWidth(),
            horizontalArrangement = spacedBy(8.dp),
        ) {
            OutlinedButton(
                modifier = Modifier
                    .recomposeHighlighter()
                    .weight(1f),
                onClick = { action(SendFeedback) },
            ) {
                Icon(
                    painter = painterResource(drawable.ic_feedback),
                    contentDescription = null
                )
                Spacer(
                    modifier = Modifier
                        .recomposeHighlighter()
                        .width(8.dp)
                )
                Text(text = stringResource(string.feedback))
            }
            IconOutlineButton(
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
private fun AboutHeaderPreview() {
    PreviewAppTheme {
        AboutHeader(AboutState("0.0.999")) {}
    }
}

@Preview
@Composable
private fun AboutHeaderDarkPreview() {
    PreviewAppTheme(theme = ThemeMode.DARK_MODE) {
        AboutHeader(AboutState("0.0.999")) {}
    }
}